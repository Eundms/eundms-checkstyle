# EUNDMS CHECKSTYLE
> 표준 코딩 컨벤션을 checkstyle 기반으로 정의하고 실행가능한 jar로 배포하여 모든 프로젝트에서 일관된 코드 스타일 검사를 수행하도록 하기 위한 프로젝트입니다.

## 요구사항
- Java 21+
- Maven 3.6+

## 개요 
1. checkstyle 
- release : https://github.com/checkstyle/checkstyle/releases
  - source code의 src/main/java/resources 내에 checkstyle 문법에 맞는 google, sun 버전 예제 포함되어 있음
2. maven-shade-plugin
- 모든 의존성 + 리소스(XML) + 실행 클래스 -> 하나의 Fat JAR 생성 

## 프로젝트 구조 
```bash
eundms-checkstyle/
 ├── pom.xml                                      # Maven 빌드 및 Fat JAR 설정
 ├── src/main/java/com/eundms/RunCheckstyle.java # 실행 진입점
 ├── src/main/resources/eundms-checkstyle.xml     # 규칙 정의 파일
 ├── src/main/resources/eundms-suppressions.xml   # 규칙 제외 파일
 └── src/test/                                    # 테스트 파일들
     ├── java/com/eundms/CheckstyleTest.java      # 규칙 검증 테스트
     └── resources/SampleViolation.java           # 테스트용 샘플 코드
```

## 사용 방법
> Nexus 서버 : https://nexus.eundms.com/repository/maven-releases
> eundms-checkstyle 버전 : ${eundms-checkstyle-version}

### 로컬 실행
- 방법1. eundms-checkstyle 프로젝트 빌드 
```bash
git clone <eundms-checkstyle-repo-url>
cd eundms-checkstyle
mvn clean package # target/eundms-checkstyle-${eundms.checkstyle.version}.jar 생성
```
- 방법2. Nexus 서버에서 다운로드 
```bash
curl -o target/eundms-checkstyle-${eundms.checkstyle.version}.jar \
  ${eundms.nexus.release.url}/com/eundms/eundms-checkstyle/${eundms.checkstyle.version}/eundms-checkstyle-${eundms.checkstyle.version}.jar
```

- 실행
```bash
java -jar target/eundms-checkstyle-${eundms-checkstyle-version}.jar
```

### maven verify 단게에서 검사 자동화 
- pom.xml
```xml
<!-- 버전, 릴리즈 url 관리 --> 
<properties>
  <eundms.checkstyle.version>1.0.0</eundms.checkstyle.version>
  <eundms.nexus.release.url>https://nexus.eundms.com/repository/maven-releases</eundms.nexus.release.url>
  <exec-maven-plugin.version>3.1.0</exec-maven-plugin.version>
</properties>

<!-- repositories -->
<repositories>
  <repository>
    <id>eundms-nexus</id>
    <url>${eundms.nexus.release.url}</url>
  </repository>
</repositories>

<!-- dependencies -->
<dependencies>
  <dependency>
    <groupId>com.eundms</groupId>
    <artifactId>eundms-checkstyle</artifactId>
    <version>${eundms.checkstyle.version}</version>
  </dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>exec-maven-plugin</artifactId>
      <version>${exec-maven-plugin.version}</version>
      <executions>
        <execution>
          <id>run-checkstyle</id>
          <phase>verify</phase> 
          <goals>
            <goal>java</goal>
          </goals>
          <configuration>
            <mainClass>com.eundms.RunCheckstyle</mainClass>
            <classpathScope>compile</classpathScope>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>

```

### pre-commit Hook 연동
- .pre-commit-config.yaml
```bash
repos:
  - repo: local
    hooks:
      - id: company-checkstyle
        name: Company Checkstyle
        entry: java -jar tools/eundms-checkstyle-${eundms-checkstyle-version}.jar
        language: system
        types: [ java ]
```
- 설치
```bash
pip install pre-commit
pre-commit install
```

## 규칙 수정 & 배포 플로우
1. Nexus 인증 설정
   - 방법1) `~/.m2/settings.xml` 파일에 Nexus 자격증명 추가
   - 방법2) 프로젝트 내 `settings.xml` 파일에 Nexus 자격증명 설정
   
   => 프로젝트 내 `settings.xml` 참고

2. 규칙 수정
   - `src/main/resources/eundms-checkstyle.xml` 변경

3. 버전 업데이트
   - `pom.xml`의 `<version>` 값 증가 

4. 빌드 및 Nexus 배포
   ```bash
   # 방법1
   mvn clean deploy
   # 방법2
   mvn clean deploy -s settings.xml
   ```
   - 버전이 `-SNAPSHOT`으로 끝나면 snapshotRepository에 배포 
   - 버전이 일반숫자이면 repository(release)에 배포 

5. 사용 프로젝트 반영
   - pre-commit / CI 설정에서 JAR 버전만 업데이트 
