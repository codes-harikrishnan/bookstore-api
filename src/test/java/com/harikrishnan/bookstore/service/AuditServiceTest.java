package com.harikrishnan.bookstore.service;
import com.harikrishnan.bookstore.enums.OrderStatus;
import com.harikrishnan.bookstore.repository.AuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void log_WithValidInputs_ShouldAddLog(){
      auditService.log(OrderStatus.PURCHASE_ATTEMPTED,"Hello");
      verify(auditRepository).save(any());
    }

}
