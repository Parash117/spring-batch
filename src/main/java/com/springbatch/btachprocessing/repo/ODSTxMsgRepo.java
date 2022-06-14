package com.springbatch.btachprocessing.repo;

import com.springbatch.btachprocessing.entity.ODSTransactionMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ODSTxMsgRepo extends JpaRepository<ODSTransactionMessage, Long> {

}
