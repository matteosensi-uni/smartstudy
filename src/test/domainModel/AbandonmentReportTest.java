package test.domainModel;

import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.enums.ReportStatus;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class AbandonmentReportTest {
    @Test
    public void testOpenSuccess() {
        AbandonmentReport abandonmentReport = AbandonmentReport.open("", 1, 1);
        assertEquals("", abandonmentReport.getDescription());
        assertNull(abandonmentReport.getAdminId());
        assertEquals(ReportStatus.OPENED, abandonmentReport.getStatus());
        assertEquals(1, abandonmentReport.getReservationId());
        assertEquals(1, abandonmentReport.getStudentId());
        assertNull(abandonmentReport.getResolvedAt());
        assertNotNull(abandonmentReport.getCreatedAt());
    }
    @Test
    public void testOpenFailure() {
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.open("", 0, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.open("", 1, 0)
        );
    }

    @Test
    public void testValueOfFailure() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime resolvedAt = LocalDateTime.now().plusMinutes(5);
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.OPENED, "", 1, 0, 2L)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.OPENED, "", 0, 1, 2L)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.OPENED, "", 1, 1, 2L)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.PENDING, "", 1, 1, null)
        );
        assertThrows(DomainViolationException.class, () ->
            AbandonmentReport.valueOf(1, createdAt, null, ReportStatus.CONFIRMED, "", 1, 1, 2L)
        );

    }

    @Test
    public void testValueOfSuccess() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime resolvedAt = LocalDateTime.now().plusMinutes(5);
        AbandonmentReport report = AbandonmentReport.valueOf(1, createdAt, resolvedAt, ReportStatus.PENDING, "", 1, 1, 2L);
        assertEquals("", report.getDescription());
        assertEquals(Long.valueOf(2), report.getAdminId());
        assertEquals(ReportStatus.PENDING, report.getStatus());
        assertEquals(1, report.getReservationId());
        assertEquals(1, report.getStudentId());
        assertEquals(resolvedAt, report.getResolvedAt());
        assertEquals(createdAt, report.getCreatedAt());
    }

    @Test
    public void  testRejectFailure() {
        assertThrows(DomainViolationException.class, () -> {
            AbandonmentReport abandonmentReport = AbandonmentReport.open("", 1, 1);
            abandonmentReport.reject(2); // manda eccezione perché non è ancora stato preso in carico
        });
    }
    @Test
    public void  testRejectSuccess() { // non si ripetono i test per confirm in quanto usa lo stesso metodo internamente
        AbandonmentReport abandonmentReport = AbandonmentReport.open("", 1, 1);
        abandonmentReport.takeInCharge(2);
        abandonmentReport.reject(2); // manda eccezione perché non è ancora stato preso in carico
        assertEquals(ReportStatus.REJECTED, abandonmentReport.getStatus());
        assertEquals(Long.valueOf(2), abandonmentReport.getAdminId());
        assertNotNull(abandonmentReport.getResolvedAt());
    }

    @Test
    public void  testReportRejectFailureWrongAdmin() {
        AbandonmentReport abandonmentReport = AbandonmentReport.open("", 1, 1);
        abandonmentReport.takeInCharge(2);
        assertThrows(DomainViolationException.class, () ->
            abandonmentReport.reject(0)
        );
    }

    @Test
    public void testRepeatedRejectFailure() {
        AbandonmentReport abandonmentReport = AbandonmentReport.open("", 1, 1);
        abandonmentReport.takeInCharge(2); // manda eccezione perché non si può modificare un report chiuso
        abandonmentReport.reject(2);
        assertNotNull(abandonmentReport.getResolvedAt()); //controllo che la data venga aggiornata
        assertEquals(ReportStatus.REJECTED, abandonmentReport.getStatus()); // controllo che lo stato venga aggiornato
        assertThrows(DomainViolationException.class, () ->
            abandonmentReport.reject(2)
        );
    }
}
