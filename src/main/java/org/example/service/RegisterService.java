package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.RegisterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegisterService {
    private final RegisterRepository registerRepository;

    @Transactional
    public List<Long> registerDocument(Long[] documentIds) {
        return registerRepository.batchInsert(documentIds);
    }
}
