package io.liuzhuoran.talentflow.service;

import io.liuzhuoran.talentflow.mapper.EmployeeMapper;
import io.liuzhuoran.talentflow.model.Employee;
import io.liuzhuoran.talentflow.model.MailConstants;
import io.liuzhuoran.talentflow.model.MailSendLog;
import io.liuzhuoran.talentflow.model.RespPageBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MailSendLogService mailSendLogService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getEmployeeByPageShouldConvertPageToOffset() {
        Employee condition = new Employee();
        Date[] beginDateScope = null;
        List<Employee> employees = Collections.singletonList(new Employee());

        when(employeeMapper.getEmployeeByPage(20, 20, condition, beginDateScope)).thenReturn(employees);
        when(employeeMapper.getTotal(condition, beginDateScope)).thenReturn(31L);

        RespPageBean result = employeeService.getEmployeeByPage(2, 20, condition, beginDateScope);

        assertEquals(31L, result.getTotal());
        assertEquals(employees, result.getData());
    }

    @Test
    void addEmpShouldComputeContractAndSendMailWhenInsertSucceeds() throws ParseException {
        Employee employee = new Employee();
        employee.setId(100);
        employee.setBeginContract(parseDate("2024-01-01"));
        employee.setEndContract(parseDate("2025-01-01"));

        Employee stored = new Employee();
        stored.setId(100);

        when(employeeMapper.insertSelective(employee)).thenReturn(1);
        when(employeeMapper.getEmployeeById(100)).thenReturn(stored);

        int insertResult = employeeService.addEmp(employee);

        assertEquals(1, insertResult);
        assertEquals(1.0, employee.getContractTerm(), 0.0001);

        ArgumentCaptor<MailSendLog> logCaptor = ArgumentCaptor.forClass(MailSendLog.class);
        verify(mailSendLogService).insert(logCaptor.capture());
        MailSendLog sendLog = logCaptor.getValue();
        assertEquals(Integer.valueOf(100), sendLog.getEmpId());
        assertNotNull(sendLog.getMsgId());
        assertTrue(sendLog.getMsgId().length() > 10);

        verify(rabbitTemplate).convertAndSend(
                eq(MailConstants.MAIL_EXCHANGE_NAME),
                eq(MailConstants.MAIL_ROUTING_KEY_NAME),
                eq(stored),
                any(CorrelationData.class)
        );
    }

    @Test
    void addEmpShouldNotSendMailWhenInsertFails() throws ParseException {
        Employee employee = new Employee();
        employee.setId(100);
        employee.setBeginContract(parseDate("2024-01-01"));
        employee.setEndContract(parseDate("2025-01-01"));

        when(employeeMapper.insertSelective(employee)).thenReturn(0);

        int insertResult = employeeService.addEmp(employee);

        assertEquals(0, insertResult);
        verify(mailSendLogService, never()).insert(any(MailSendLog.class));
        verify(rabbitTemplate, never()).convertAndSend(
                eq(MailConstants.MAIL_EXCHANGE_NAME),
                eq(MailConstants.MAIL_ROUTING_KEY_NAME),
                any(Employee.class),
                any(CorrelationData.class)
        );
    }

    @Test
    void getEmployeeByPageWithSalaryShouldReturnDataAndTotal() {
        List<Employee> employees = Collections.singletonList(new Employee());
        when(employeeMapper.getEmployeeByPageWithSalary(0, 10)).thenReturn(employees);
        when(employeeMapper.getTotal(null, null)).thenReturn(8L);

        RespPageBean result = employeeService.getEmployeeByPageWithSalary(1, 10);

        assertEquals(8L, result.getTotal());
        assertEquals(employees, result.getData());
    }

    private Date parseDate(String value) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(value);
    }
}
