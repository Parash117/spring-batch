package com.springbatch.btachprocessing.config;

import com.springbatch.btachprocessing.entity.ODSTransactionMessage;
import com.springbatch.btachprocessing.listner.JobCompletionListener;
import com.springbatch.btachprocessing.processor.ODSTransactionProcessor;
import com.springbatch.btachprocessing.repo.ODSTxMsgRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import javax.sql.DataSource;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobCompletionListener jobCompletionNotificationListener;
    @Autowired
    private ODSTxMsgRepo odsTxMsgRepo;
    @Autowired
    private DataSource dataSource;

    private final Map<Object, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();
    private AtomicBoolean enabled = new AtomicBoolean(true);
    private AtomicInteger batchRunCounter = new AtomicInteger(0);

    @Bean
    public JsonItemReader<ODSTransactionMessage> reader(){
        return new JsonItemReaderBuilder<ODSTransactionMessage>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(ODSTransactionMessage.class))
                .resource(new ClassPathResource("odstransaction-message.json"))
                .name("ODSTransactionReader")
                .build();
    }
    @Bean
    public ODSTransactionProcessor processor(){
        return new ODSTransactionProcessor();
    }

    @Bean
    public RepositoryItemWriter<ODSTransactionMessage> writer() {
        RepositoryItemWriter<ODSTransactionMessage> iwriter = new RepositoryItemWriter<>();
        iwriter.setRepository(odsTxMsgRepo);
        iwriter.setMethodName("save");
        return iwriter;
    }

    @Bean
    public Step stepOne(){
        return stepBuilderFactory.get("stepOne")
                .<ODSTransactionMessage, ODSTransactionMessage>chunk(20)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    @Bean
    public Job importUserJob(JobCompletionListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
//                .flow(step1)
//                .end()
                .start(step1)
                .build();
    }
    /*@Scheduled(cron="0 0 0 25 1/1 ? *")
    public void launchJob() throws Exception {
        Date date = new Date();
        log.debug("scheduler starts at " + date);
        if (enabled.get()) {
            JobExecution jobExecution = jobLauncher.run(importUserJob(jobCompletionNotificationListener, stepOne()), new JobParametersBuilder().addDate("launchDate", date)
                    .toJobParameters());
            batchRunCounter.incrementAndGet();
            log.debug("Batch job ends with status as " + jobExecution.getStatus());
        }
        log.debug("scheduler ends ");
    }*/
    //    @Bean public JdbcBatchItemWriter writer(DataSource dataSource) { return new JdbcBatchItemWriterBuilder()
//            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//            .sql("INSERT INTO coffee (brand, origin, characteristics) VALUES (:brand, :origin, :characteristics)")
//            .dataSource(dataSource) .build(); }\
//    @Bean
//    public MongoItemWriter<ODSTransactionMessage> writer(MongoTemplate mongoTemplate) {
//        return new MongoItemWriterBuilder<ODSTransactionMessage>().template(mongoTemplate).collection("ODSTransactionMessage")
//                .build();
//    }




    @Bean
    public TaskScheduler poolScheduler() {
        return new CustomTaskScheduler();
    }
    private class CustomTaskScheduler extends ThreadPoolTaskScheduler {
        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
            ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);
            ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
            scheduledTasks.put(runnable.getTarget(), future);
            return future;
        }
    }

    public void cancelFutureSchedulerTasks() {
        scheduledTasks.forEach((k, v) -> {
            if (k instanceof BatchConfig) {
                v.cancel(false);
            }
        });
    }


}
