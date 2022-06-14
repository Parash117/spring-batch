package com.springbatch.btachprocessing.processor;


import com.springbatch.btachprocessing.entity.ODSTransactionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ODSTransactionProcessor implements ItemProcessor<ODSTransactionMessage, ODSTransactionMessage> {

    @Override
    public ODSTransactionMessage process(final ODSTransactionMessage odsTransactionMessage) throws Exception {
        log.info("Transaction processed for {}", odsTransactionMessage.getId());
        return odsTransactionMessage;
    }
}
