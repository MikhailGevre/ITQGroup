package org.example.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.DocumentController;
import org.example.dto.DocumentApproveDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchedulerSubmit {
    private final DocumentController controller;

    @Scheduled(cron = "${scheduled.submit.cron}")
    public void workerSubmit() {
        List<Long> documentsIds = controller.getBatchByStatus("DRAFT");
        controller.approve(new DocumentApproveDto(documentsIds.toArray(new Long[0])));

    }
}
