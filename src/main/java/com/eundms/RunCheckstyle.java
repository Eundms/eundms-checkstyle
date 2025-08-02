package com.eundms;
import com.puppycrawl.tools.checkstyle.Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

public class RunCheckstyle {
  public static void main(String[] args) throws Exception {
    // 1. 규칙 파일과 제외 파일을 임시 위치로 추출
    File suppressionsFile = extractResource("/eundms-suppressions.xml");
    File configFile = extractResource("/eundms-checkstyle.xml");

    // 2. 규칙 파일 내에서 suppressions.xml 경로를 동적으로 바꾸기
    replaceSuppressionsPath(configFile, suppressionsFile);

    // 3. Checkstyle 실행 인자
    String targetDir = (args.length > 0) ? args[0] : "src/main/java";
    String[] checkstyleArgs = {
        "-c", configFile.getAbsolutePath(),
        targetDir
    };

    // 4. Checkstyle 실행
    Main.main(checkstyleArgs);
  }

  private static File extractResource(String resourcePath) throws Exception {
    File tempFile = Files.createTempFile("checkstyle-", ".xml").toFile();
    try (InputStream in = RunCheckstyle.class.getResourceAsStream(resourcePath);
        FileOutputStream out = new FileOutputStream(tempFile)) {
      if (in == null) {
        throw new RuntimeException("Resource not found: " + resourcePath);
      }
      in.transferTo(out);
    }
    tempFile.deleteOnExit();
    return tempFile;
  }

  private static void replaceSuppressionsPath(File configFile, File suppressionsFile) throws Exception {
    // 규칙 XML에서 suppressions.xml 경로를 실제 임시 파일 경로로 치환
    String content = Files.readString(configFile.toPath());
    content = content.replace("suppressions.xml", suppressionsFile.getAbsolutePath());
    Files.writeString(configFile.toPath(), content);
  }
}
