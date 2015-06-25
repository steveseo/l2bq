l2bq - 로그를 Big Query로

# 한글판 읽어보기 / [English](https://github.com/kevinseo/l2bq/blob/master/README.md)
 - 영어로 아무리 내용을 작성한다고 한들 한글만큼 그 뜻을 제대로 전달하기가 어려워 한글로 도움말을 작성

# 데모 화면
 - 이 프로젝트가 무엇을 하는지 잘 모르겠는 분을 위해서 데모 사이트를 준비했습니다.
 - [데모 사이트](http://l2bq-test.appspot.com/dashboard/index.html)

# 프로젝트 설명
 - 이 프로젝트는 구글 앱 엔진을 사용하여 서블릿이 남기는 App Log를 통해 로그 분석 시스템을 구축하고자 하는 팀을 위한 Java로 작성된 라이브러리입니다.
 - 이 프로젝트는 Mache framework(https://github.com/StreakYC/mache)에서부터 시작되었고, Mache Framework가 HTTP 서블릿 요청만을 기록하는 제약사항을 넘어서기 위해 만들었습니다. 해당 프로젝트에 직접적으로 기여하는 방법도 생각해 보았지만 우리가 생각하는 방향과 다를 수 있을것 같아 별도의 프로젝트를 유지하기로 결정하였습니다.
 - 사용자 로그를 처리하기 위해서 시스템 아키텍처를 개선하였으며 예제로 사용할 익스포터(Exporter)들을 추가하였습니다.
 - l2bq는 총 두개의 프로젝트로 구성되어 있습니다. 하나는 여러분이 보고 계시는 [l2bg](https://github.com/kevinseo/l2bq)(Java 라이브러리 프로젝트)이며, 다른 하나는 이 라이브러리의 사용 방법을 보여주기 위해 만든 [l2bq-sample](https://github.com/wishoping/l2bq-sample)(Google App Engine 프로젝트)입니다. l2bq는 kevinseo(wooseok.seo@gmail.com)이, l2bq-sample은 jkkim(wishoping@gmail.com)이 주 개발자로 등록되어 있습니다.
 - 간단히 말하자면, log4j 로그를 분석하는 도구입니다.
 - 이 프로젝트는 Google App Engine 기반의 서버를 구축한 모바일 소셜 게임 회사에 관리자 도구를 만들어주기 위해 시작하게 되었습니다. 그런 이유로 l2bq-sample에서는 DAU와 MAU, Contention Rate와 같은 그래프를 확인할 수 있도록 예제를 구성하였습니다.
 - 우리는 이 프로젝트가 고가의 분석 시스템을 구입/구축하기에 앞서 최소한의 노력으로 Google App Engine 기반의 로그 분석 시스템을 구축하는데 도움을 주기를 바라고 있습니다.
 - Mache Framework에 대해 친절하게 분석해 놓은 자료는 [Mache Framework의 구조 분석(jkkim 작성)](https://drive.google.com/folderview?id=0Bxujl8fMG4jSamFvRGpHNkN5akU&usp=sharing)에서 참고
 - Mache Framework로부터 개선한 내용은 [주요 개선 사항](https://github.com/kevinseo/l2bq/wiki/%EC%A3%BC%EC%9A%94-%EA%B0%9C%EC%84%A0-%EC%82%AC%ED%95%AD)을 참고
 - 우리는 무엇보다도 이 프로젝트의 기반이 된 Mache Framework의 제작사인 Streak.com에 매우 감사하고 있습니다.
 
# 무엇에 쓰는 물건인고?

l2bq 프레임워크는 App Engine의 로그를 BigQuery로 익스포트하는 Java App Engine 라이브러리입니다. 프레임워크는 주기적으로 App Engine의 LogService로부터 로그를 BigQuery 테이블로 복사하는 cron 작업으로 구성되어 있으며, 사용자가 임의로 로그 파일들을 분석하여 BigQuery 테이블에 저장할 수 있는 방법을 제공합니다.

# 작동 방식

Google App Engine에서 [LogService API](https://developers.google.com/appengine/docs/java/logservice/)를 통해 서블릿 요청에 접근할 수 있습니다. 이 프레임워크는 짧은 주기로 반복되는(2분, 변경 가능) cron 작업으로부터 작업을 시작합니다. cron 작업은 마지막으로 익스포트된 로그 엔트리로부터 지난 시간을 계산하여 정해진 시간 단위(*msPerFile* 매개 변수로 지정)로 처리해야할 로그들을 나누고, 각각에 대해서 익스포트 작업을 처리합니다.

익스포트 처리 작업은 크게 두 단계로 나뉩니다. 각 작업은 별도의 테스크 큐(task queue)로 실행됩니다.

- 첫번째 단계는 App Engine LogService에서 로그 파일을 가져와 Google Cloud Storage에 CSV 파일(schema 포함)로 익스포트합니다.
- 두번째 단계는 CSV 파일을 BigQuery 테이블에 저장하기 위한 BigQuery 작업을 실행합니다.

이 과정을 도식화하면 다음과 같습니다.

![실행 과정](https://raw.github.com/kevinseo/l2bq/master/mache_diagram.png)

그래프에서 Y축은 로그가 처리되는 시간(위에서 아래로 증가)이며, X축은 처리 단계(왼쪽에서 오른쪽으로 실행)입니다. 각각의 cron 작업(왼쪽 끝 열)은 마지막으로 익스포트된 이후로 지난 시간을 나타냅니다. 만약 충분한 시간이 지났다면(*msPerFile* 매개 변수로 정의한 값. 예제에서는 2000ms), 새로운 익스포트 작업을 실행할 준비가 되어, cron 작업은 그동안 쌓이 로그를 CSV 파일로 기록하기 위해 새로운 *storeLogsInCloudStorage* 작업을 시작합니다. 이 작업은 다음 단계에서 생성한 CSV 파일을 BigQuery로 로드하기 위해 *loadCloudStorageToBigquery* 작업을 시작합니다.

복수의 파일들이 *msPerTable* 매개 변수(예제에서는 4000ms)에 의해서 정의된 기간만큼 동일한 테이블에 기록됩니다. 만약 *msPerFile*이 *msPerTable*로 나뉠 수 없다면 하나의 파일이 여러개의 테이블로 쪼개 질 것입니다. 예를 들어, *msPerFile*이 3000이고, *msPerTable*이 4000이면 3초부터 6초에서 발생한 로그는 0초부터 4초를 기록한 테이블과 4초에서 8초를 기록한 테이블로 나뉘어 져야 합니다. 하지만 이 기능은 아직 구현되지 않았습니다. *msPerTable*과 *msPerFile*에 관한 제약 사항은 "매개 변수의 변경" 섹션에서 자세하게 설명하고 있습니다.

## Cloud Storage로 익스포트하기
처음에 시작되는 StoreLogsInCloudStorageTask 작업은 LogService로부터 모든 요청 로그를 처리합니다. 이 함수는 각각의 로그를 CSV의 한 줄로 파싱하기 위해 사용자 정의 익스포터의 집합(set)을 사용합니다. 처리된 로그는 Google Cloud Storage에 저장됩니다. 각 파일에 저장되는 분량은 *msPerFile* 변수에 의해서 지정됩니다. 만약 이 값을 낮게하면 로그 이벤트가 발생한 시간과 BigQuery에 익스포트되는 시간의 간격이 줄어들겠지만 익스포트 작업이 빈번하게 발생하므로 응용 프로그램의 리소스를 더욱 많이 사용하게 됩니다. 하지만 BigQuery는 하루에 최대 1,000 로드(쓰기)만 가능하므로 *msPerFile* 값을 2분보다 작게하면 아마도 BigQuery의 제약에 걸릴 것입니다.

실행되는 익스포터들은 BigqueryFieldExporterSet에 정의되어 있습니다. 이 프레임워크는 기본 요청 로그의 거의 모든 정보를 익스포트하는 기본 익스포터 세트를 제공하고 있습니다. 여러분이 작성한 응용 프로그램의 로그를 처리하기 위한 익스포터를 작성하고 싶다면, "익스포터 개발하기" 섹션을 살펴보세요.

## BigQuery에 저장하기
두번째로 실행되는 LoadCloudStorageToBigqueryTask 작업은 CSV 파일을 로드하는 BigQuery 로드 작업을 시작합니다. BigQuery 클라이언트가 쪼개진 여러개의 테이블에서 쿼리를 하기는 여간 불편한일이기 때문에, 프레임워크에서는 여러개의 파일을 테이블 하나로 통합하는 기능을 제공합니다. 테이블로 통합하는 시간 간격은 *msPerTable* 변수에 정의합니다.

# 설치
 - github 저장소에서 clone 합니다.

```
$ git clone {insert repo url} 
```

 - l2bq-0.0.1.jar를 서블릿 프로젝트의 war/WEB-INF/lib 디렉터리에 복사합니다. 그리고 Eclipse 빌드 경로에 jar 파일을 추가합니다.
 - 만약 작업 중인 프로젝트가 BigQuery를 사용하고 있지 않았다면, 프로젝트 의존성에 BigQuery를 추가해야 합니다. 프로젝트에서 마우스 오른쪽 버튼을 클릭한 후 Google->Add Google APIs->BigQuery API를 선택합니다. 이 옵션을 사용하기 위해서는 최신 버전의 Google Eclipse Plugin이 필요합니다.
 - 다음 코드를 war/WEB-INF/web.xml 파일에 추가하여 서블릿이 호출되도록 합니다.

```
<servlet>
  <servlet-name>LogExportCronTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.LogExportCronTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>LogExportCronTask</servlet-name>
  <url-pattern>/logging/logExportCron</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>StoreLogsInCloudStorageTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.StoreLogsInCloudStorageTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>StoreLogsInCloudStorageTask</servlet-name>
  <url-pattern>/logging/storeLogsInCloudStorage</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>LoadCloudStorageToBigqueryTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.LoadCloudStorageToBigqueryTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>LoadCloudStorageToBigqueryTask</servlet-name>
  <url-pattern>/logging/loadCloudStorageToBigquery</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>DeleteCompletedCloudStorageFilesTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.DeleteCompletedCloudStorageFilesTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>DeleteCompletedCloudStorageFilesTask</servlet-name>
  <url-pattern>/logging/deleteCompletedCloudStorageFilesTask</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>BigqueryStatusServlet</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.BigqueryStatusServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>BigqueryStatusServlet</servlet-name>
  <url-pattern>/logging/bigqueryStatus</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>DatastoreExportServlet</servlet-name>
  <servlet-class>com.l2bq.datastore.analysis.DatastoreExportServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>DatastoreExportServlet</servlet-name>
  <url-pattern>/logging/datastoreExport</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>BuiltinDatastoreToBigqueryCronTask</servlet-name>
  <servlet-class>com.l2bq.datastore.analysis.builtin.BuiltinDatastoreToBigqueryCronTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>BuiltinDatastoreToBigqueryCronTask</servlet-name>
  <url-pattern>/bqlogging/builtinDatastoreExport</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>BuiltinDatastoreToBigqueryIngestorTask</servlet-name>
  <servlet-class>com.l2bq.datastore.analysis.builtin.BuiltinDatastoreToBigqueryIngesterTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>BuiltinDatastoreToBigqueryIngestorTask</servlet-name>
  <url-pattern>/bqlogging/builtinDatastoreToBigqueryIngestorTask</url-pattern>
</servlet-mapping>

<security-constraint>
  <web-resource-collection>
    <url-pattern>/logging/*</url-pattern>
  </web-resource-collection>
  <auth-constraint>
    <role-name>admin</role-name>
  </auth-constraint>
</security-constraint>
```

# 등록
Cloud Storage와 BigQuery를 https://code.google.com/apis/console/ 에서 등록합니다.

API를 사용하기 위해서는 결제(billing) 탭에서 결제를 활성화해야 합니다.

## App Engine를 Cloud Storage와 BigQuery에 등록하기
### Cloud Storage
https://developers.google.com/storage/docs/gsutil_install 에서 gsutil을 다운로드하고 다음 명령을 실행합니다.

gsutil을 사용한 적이 없다면, 다음 명령으로 gsutil이 Google Cloud Storage 계정에 접근할 수 있도록 합니다.
```
$ gsutil config
```

버킷(bucket) 생성:
```
$ gsutil mb gs://{버킷 이름}
```

버킷의 ACL을 파일로 저장:
```
$ gsutil getacl gs://{버킷 이름} > bucket.acl
```

다음 코드를 bucket.acl의 첫번째 <Entry> 태그 바로 앞에 추가:
```
<Entry>
  <Scope type="UserByEmail">
     <EmailAddress>
        {앱 아이디(appid)}@appspot.gserviceaccount.com
     </EmailAddress>
  </Scope>
  <Permission>
     WRITE
  </Permission>
</Entry>
```

변경한 ACL을 버킷에 로드:
```
$ gsutil setacl bucket.acl gs://{버킷 이름}
```

### BigQuery
1. Google APIs console 사이트(https://code.google.com/apis/console/)를 방문합니다.
2. Team 탭으로 이동합니다.
3. "Add a teammate:" 필드에 {앱 아이디(appid)}@appspot.gserviceaccount.com 를 입력하고 적절한 권한(is owner?)를 지정합니다.

#### BigQuery 데이터세트(dataset) 생성
1. BigQuery browser tool 사이트(https://bigquery.cloud.google.com/)를 방문합니다.
2. Google APIs 프로젝트 오른쪽에 보이는 하늘색 삼각형 아이콘을 클릭합니다.
3. "Create new dataset"을 선택한 후 이름을 입력합니다. 이 이름이 BigQuery 데이터세트 ID입니다.

#### Google APIs project ID 구하기
1. Google APIs console 사이트(https://code.google.com/apis/console/)를 방문하여 Google Cloud Storage 탭을 선택하고 "x-goog-project-id:"에 있는 숫자를 확인한다. 이 숫자가 Goole APIs project ID이다.

# 테스트 및 사용
## cron 작업 테스트
다음 링크를 사용하여 테스트:  
http://{앱 ID}.appspot.com/logging/logExportCron?bucketName={버킷 이름}&bigqueryProjectId={Google APIs project id}&bigqueryDatasetId={bigquery 데이터세트 id}

## 정기적으로 작동하도록 cron 작업 등록
아래의 내용을 cron.xml 파일에 추가한다. *msPerFile* 매개 변수의 내용을 변경하면 그에 따라 schedule 값도 변경해야 할 것이다.
```
<cron>
  <url>/logging/logExportCron?bucketName={버킷 이름}&amp;bigqueryProjectId={Google APIs project id}&amp;bigqueryDatasetId={bigquery 데이터세트 id}</url>
  <description>Export logs to BigQuery</description>
  <schedule>every 2 minutes</schedule>
</cron>
```

# 익스포트 커스터마이징
## logExportCron에 사용할 매개 변수들
익스포트 작업을 커스터마이징하는 과정은 logExportCron 서블릿에 전달되는 쿼리 문자열들을 통해서 처리된다. 한가지 주의할 점은 cron으로 등록할 때 반드시 & 기호는 &amp;로 지정해줘야 한다. 브라우저로 직접 접근할 때는 상관없다.

 - **bigqueryProjectId** BigQuery에서 사용하는 Google APIs project ID (64비트 정수형). API 콘솔에서 확인. 필수항목.
 - **bigqueryDatasetId** 테이블을 생성할 BigQuery dataset ID (사용자가 만든 문자열). 기본값: logsdataset
 - **bigqueryFieldExporterSet** 로그를 파싱할 때 사용하는 BigqueryFieldExporter들을 정의한 BigqueryFieldExporterSet 클래스의 **완전한** 클래스 이름. 기본값: com.l2bq.logging.analysis.example.BasicFieldExporterSet
 - **bucketName** csv 파일에 사용할 Cloud Storage 버킷 이름. 필수항목.
 - **queueName** 익스포트 작업에 사용할 Google App Engine 큐(queue). 기본값: Google App Engine의 기본 큐.
 - **msPerFile** 하나의 csv 파일로 통합되는 로그의 시간 간격(밀리세컨드). 이 시간 간격동안에 처리되는 로그는 작업 요청 시간 제한(10분) 내에 처리되어져야 한다. 이 시간 간격을 사용하기 위해서는 cron.xml도 함께 변경해야 한다. 또한 구조적인 이유로 인해 이 값은 *msPerTable*를 나머지 없이 나눌 수 있는 값으로 지정해야 한다. **msPerFile 또는 msPerTable 값을 변경하기에 앞서 반드시 "매개 변수 변경" 섹션을 읽어보도록 한다. 그렇지 않으면 데이터 손실이 발생할 수 있다**. 기본값: 120000 (= 2 분).
 - **msPerTable** 하나의 BigQuery 테이블로 통합되는 로그 파일의 시간 간격. 기본값: 86400000 (= 1 일)
 - **logLevel** 익스포트할 최소 로그 레벨. 다음 값 중 하나: ALL, DEBUG, ERROR, FATAL, INFO, WARN. 기본값: ALL

대부분의 매개 변수들은 LogExportCronTask에 있는 getDefault 메서드를 변경해서 설정할 수 있으므로, 원하는 값을 변경한 후 jar 파일을 다시 컴파일한다.

## 익스포터 개발하기
로그로 남겨진 값은 아래의 데이터 타입으로 익스포트할 수 있다:
 - **string** 최대 64k
 - **integer**
 - **float**
 - **boolean**

com.l2bq.logging.analysis.BigqueryFieldExporter를 통해 익스포트할 필드를 정할 수 있다. 하나의 스키마에 대해서 로그를 익스포트할 때마다 다음 메서드들이 호출된다:
 - **getFieldCount()** 은 해당 익스포트가 파싱할 필드의 수를 반환한다.
 - **getFieldName(int)** 은 0부터 getFieldCount() - 1 사이의 값을 받아서 해당 위치의 필드 이름을 반환한다. 순서는 중요하지 않지만 *getFieldType()*와 쌍을 이뤄야 한다.
 - **getFieldType(int)** 은 0부터 getFieldCount() - 1 사이의 값을 받아서 해당 위치의 필드 이름을 반환한다. 순서는 중요하지 않지만 *getFieldName()*와 쌍을 이뤄야 한다.

또한 로그 엔트리에 대해서 다음 메스드가 호출된다.
 - **processLog(RequestLogs)** 은 com.google.appengine.api.log.RequestLogs 인스턴스를 매개 변수로 받아서 필드를 추출한다. 이 메스드가 호출되고 난 후 파싱된 필드 값을 얻기 위해 *getField* 함수들이 호출된다.

*processLog(RequestLogs)* 가 호출되고 난 후 스키마에 있는 각 필드에 대해 다음 메서드가 호출된다:
 - **getField(String)** 은 필드 이름에 해당하는 값을 반환한다. 필드 이름은 문자열로 비교한다. 반환하는 값의 타입은 반드시 *getFieldType* 에서 반환한 데이터 타입과 일치해야 하지만, *toString()* 을 통해 BigQuery가 파싱할 수 있는 정도의 호환성을 유지하는 타입이면 된다(예를 들어, integer의 경우 Integer와 Long 모두 처리 가능). 만약 필드를 파싱하는데 오류가 발생하면 null을 반환하고 곧바로 익스포트가 취소된다. 값이 없는 경우에는 빈 문자열을 반환한다.

본인이 개발한 BigqueryFieldExporter를 실행하기 위해서는 com.l2bq.logging.analysis.BigqueryFieldExporterSet 를 구현한다. 이 클래스는 하나의 메서드만 제공한다.
 - **getExporters()** 는 BigqueryFieldExporters의 목록을 반환한다.

이제 BigqueryFieldExporterSet 의 전체 경로를 cron URL의 *bigqueryFieldExporterSet* 매개 변수로 전달한다. 여기서 제공하는 예제는 com.l2bq.logging.analysis.example 패키지에 있다.

## 로그 분석용 AppLog 익스포터들
기본 HTTP 서블릿 익스포터 이외에 사용자가 직접 남기는 AppLog를 처리하기 위해 com.l2bq.logging.analysis.exporter.applog.login.LoginExporter 와 com.l2bq.logging.analysis.exporter.applog.signup.SignupExporter 가 제공된다. 이 두 익스포터들은 다음과 같은 포맷의 App Log를 처리한다.

```
AppLog  {"type":"login","data":{"time":1369028411681,"userId":42,"userName":"42","langType":0,"clientVer":"1.1.12","osType":0}}
```

```
AppLog  {"type":"signup","data":{"time":1369028411681,"userId":42,"userType":1,"userName":"42","langType":0,"osType":0,"phone":"000-0000-0000","utcOffset":9}}
```

이 포맷은 여러분이 원하는 형태로 얼마든지 변경할 수 있다. 또한 로그 작성은 [l2bq-sample](https://github.com/wishoping/l2bq-sample) 프로젝트에서 확인할 수 있다.

만약 추가적으로 다양한 포맷을 지원하고 싶다면 Exporter와 ExporterSet을 구현하면 된다.

## 매개 변수의 변경
현재 구현상으로는 하나의 파일을 여러개의 테이블로 쪼개는 기능은 제공하고 있지 않다. 따라서 *msPerTable*은 반드시 *msPerFile*의 배수여야 한다.

일반적으로 *msPerTable* 나 *msPerFile*를 변경하면 *msPerTable* 시간이 지날 때까지 상황에 따라 익스포트되는 내용에 누락이나 중복이 발생할 수 있다. 이와 같은 상황을 피한채로 해당 값을 변경하는 유일한 방법은 *msPerFile*을 이번 값의 배수인 값을 변경하는 방법 뿐이다.

# 빌드
Eclipse 프로젝트는 자동으로 여러분이 설치한 App Engine SDK를 사용할 것이다. maven 프로젝트로 구성되어 있으므로 의존성 문제는 발생하지 않을 것이다.

# Datastore Entity를 BigQuery로 익스포트
이 기능을 구현하기 위해서 작업을 하고 있던 중, 최근에 Google이 Datastore의 백업 데이터를 BigQuery로 익스포트하는 기능을 공개하였다. 하지만 이 기능은 수작업으로 처리된다. l2bq는 원하는 entity를 백업하고 자동으로 BigQuery에 익스포트하는 기능을 구현하였다.

## Datastore의 데이터를 BigQuery로 익스포트
1. l2bq JAR를 프프젝트에 추가한다.
2. 앞서 소개한 서블릿 경로를 web.xml에 추가한다.
3. <code>BuiltinDatastoreExportConfiguration</code>를 구현한 클래스를 생성한다.
4. <code>/bqlogging/builtinDatastoreExport?builtinDatastoreExportConfig=</code><구현한 클래스의 전체 경로>을 호출한다.

이 URL을 cron.xml에 추가하면 주기적으로 백업할 수 있다. <code>BuiltinDatastoreExportConfiguration</code>에 대한 보다 자세한 내용은 문서를 참고한다.
