package test.domainModel;

import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Student;
import com.smartstudy.domainModel.enums.ReportStatus;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class AbandonmentReportTest {
    private Student s;
    private Admin admin1;
    private Admin admin2;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private AbandonmentReport openedAbandonmentReport;
    @Before
    public void setUp(){
        s = Student.valueOf(1, "Mario", "Rossi", "1234", "m.r@test.it", true);
        createdAt = LocalDateTime.now();
        resolvedAt = LocalDateTime.now().plusMinutes(5);
        admin1 = Admin.valueOf(1, "Mario", "Rossi", "pwd", "email", false);
        admin2 = Admin.valueOf(2, "Mario", "Rossi", "pwd", "email", false);
        openedAbandonmentReport = AbandonmentReport.open("", s);
    }


    @Test
    public void testOpenSuccess() {
        AbandonmentReport abandonmentReport = AbandonmentReport.open("", s);
        assertEquals("", abandonmentReport.getDescription());
        assertNull(abandonmentReport.getAdmin());
        assertEquals(ReportStatus.OPENED, abandonmentReport.getStatus());
        assertEquals(s, abandonmentReport.getAuthor());
        assertNull(abandonmentReport.getResolvedAt());
        assertNotNull(abandonmentReport.getCreatedAt());
        assertTrue(abandonmentReport.isActive());
    }
    @Test
    public void testOpenFailure() {
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.open("", null)
        );
    }

    @Test
    public void testValueOfFailure() {
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(0, createdAt, resolvedAt, ReportStatus.OPENED, "", s, null)
        );
        assertThrows(DomainViolationException.class, () ->
                AbandonmentReport.valueOf(1, null, resolvedAt, ReportStatus.OPENED, "", s, null)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.OPENED, "", null, null)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.PENDING, "", s, null)
        );
        assertThrows(DomainViolationException.class, () ->
                AbandonmentReport.valueOf(1, createdAt, resolvedAt, null, "", s, admin1)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.PENDING, "", s, null)
        );
        assertThrows(DomainViolationException.class, () ->
                AbandonmentReport.valueOf(1, resolvedAt, createdAt, ReportStatus.PENDING, "", s, admin1)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, null, ReportStatus.CLOSED, "", s, admin1)
        );

    }

    @Test
    public void testValueOfSuccess() {
        AbandonmentReport report = AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.PENDING, "", s, admin1);
        assertEquals("", report.getDescription());
        assertEquals(admin1.getId(), report.getAdmin().getId());
        assertEquals(ReportStatus.PENDING, report.getStatus());
        assertEquals(s.getId(), report.getAuthor().getId());
        assertEquals(resolvedAt, report.getResolvedAt());
        assertEquals(createdAt, report.getCreatedAt());
    }

    @Test
    public void  testRejectFailure() {
        assertThrows(DomainViolationException.class, () -> {
            openedAbandonmentReport.reject(admin1); // manda eccezione perché non è ancora stato preso in carico
        });
    }

    @Test
    public void  testTakeInChargeFailure() { // non si ripetono i test per confirm in quanto usa lo stesso metodo internamente
        openedAbandonmentReport.takeInCharge(admin1);
        assertThrows(DomainViolationException.class, () ->
                openedAbandonmentReport.takeInCharge(admin2)
        );
    }

    @Test
    public void  testRejectSuccess() { // non si ripetono i test per confirm in quanto usa lo stesso metodo internamente
        openedAbandonmentReport.takeInCharge(admin1);
        openedAbandonmentReport.reject(admin1); // manda eccezione perché non è ancora stato preso in carico
        assertEquals(ReportStatus.REJECTED, openedAbandonmentReport.getStatus());
        assertEquals(admin1.getId(), openedAbandonmentReport.getAdmin().getId());
        assertNotNull(openedAbandonmentReport.getResolvedAt());
    }

    @Test
    public void  testReportRejectFailureWrongAdmin() {
        openedAbandonmentReport.takeInCharge(admin1);
        assertThrows(DomainViolationException.class, () ->
                openedAbandonmentReport.reject(admin2)
        );
    }

    @Test
    public void  testReportRejectFailureNullAdmin() {
        openedAbandonmentReport.takeInCharge(admin1);
        assertThrows(DomainViolationException.class, () ->
                openedAbandonmentReport.reject(null)
        );
    }

    @Test
    public void testRepeatedRejectFailure() {
        openedAbandonmentReport.takeInCharge(admin1); // prende in carico il report, ora è gestibile da admin1
        openedAbandonmentReport.reject(admin1);
        assertNotNull(openedAbandonmentReport.getResolvedAt()); //controllo che la data venga aggiornata
        assertEquals(ReportStatus.REJECTED, openedAbandonmentReport.getStatus()); // controllo che lo stato venga aggiornato
        assertThrows(DomainViolationException.class, () ->
                openedAbandonmentReport.reject(admin1)
        );
    }
}
