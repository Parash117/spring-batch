package com.springbatch.btachprocessing.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ods_transaction_manager")
public class ODSTransactionMessage {
    @Id
    @GeneratedValue
    private Long uid;

    private Long id;

    private String crn;

    private Double totalPointsEarned;

    private String bonusCode;

    private String cycleDate;

    private String processedDate;

    private String destinationSystem;

    @Override
    public String toString() {
        return "ODSTransactionMessage{" +
                "id=" + id +
                ", crn='" + crn + '\'' +
                ", totalPointsEarned=" + totalPointsEarned +
                ", bonusCode='" + bonusCode + '\'' +
                ", cycleDate='" + cycleDate + '\'' +
                ", processedDate='" + processedDate + '\'' +
                ", destinationSystem='" + destinationSystem + '\'' +
                '}';
    }
}
