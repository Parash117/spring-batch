package com.springbatch.btachprocessing.listner;

import com.springbatch.btachprocessing.repo.ODSTxMsgRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

    private final ODSTxMsgRepo odsTxMsgRepo;

    public JobCompletionListener(ODSTxMsgRepo odsTxMsgRepo) {
        this.odsTxMsgRepo = odsTxMsgRepo;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("One job finished.");
//            odsTxMsgRepo.findAll().stream().forEach(x-> System.out.println(x.toString()));
        }
    }
}
