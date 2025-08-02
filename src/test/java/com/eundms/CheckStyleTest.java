package com.eundms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

class CheckstyleTest {

  @Test
  void testCheckstyleRules() throws Exception {
    Configuration config = ConfigurationLoader.loadConfiguration(
        new InputSource(Files.newInputStream(Path.of("src/main/resources/eundms-checkstyle.xml"))),
        new PropertiesExpander(System.getProperties()),
        ConfigurationLoader.IgnoredModulesOptions.OMIT);

    Checker checker = new Checker();
    checker.setModuleClassLoader(Checker.class.getClassLoader());
    checker.configure(config);

    File sampleFile = new File("src/test/resources/SampleViolation.java");
    assertTrue(sampleFile.exists(), "샘플 파일이 존재해야 합니다");

    TestAuditListener listener = new TestAuditListener();
    checker.addListener(listener);

    checker.process(List.of(sampleFile));

    assertFalse(listener.violations.isEmpty(), "규칙 위반이 감지되어야 합니다.");
    checker.destroy();
  }

  private static class TestAuditListener implements AuditListener {
    List<AuditEvent> violations = new java.util.ArrayList<>();

    @Override
    public void auditStarted(AuditEvent auditEvent) {

    }

    @Override
    public void auditFinished(AuditEvent auditEvent) {

    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {

    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {

    }

    @Override
    public void addError(AuditEvent event) {
      violations.add(event);
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable throwable) {

    }
  }
}
