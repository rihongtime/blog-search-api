# 블로그 검색 API

## 기능 명세
1. 블로그 검색
    - 카카오 API 검색
    - 실패시 네이버 API 검색
2. 인기 검색어 목록

## API 명세
#### Response Type
- 성공
```
  {
    "result": "SUCCESS",
    "data": T
  }
``` 
- 실패
```
  {
    "result": "ERROR",
    "data": {
        "code": "UNKNOWN",
        "message": "Unknown Error"
    }
  }
``` 

### API

#### 블로그 검색 API
`GET /v1/search/blog`

- request parameter
  - query : 검색어(필수)
    - String
  - sort : 결과 문서 정렬 방식(선택) 
    - String
    - ACCURACY(정확도) or RECENCY(최신순)
  - page : 결과 페이지 번호(선택)
    - Integer
    - 가능한 범위 1-50
  - size : 한 페이지에 보여질 문서 수(선택)
    - Integer
    - 가능한 범위 1-50

```
curl -X 'GET' \
  'http://localhost:8080/v1/search/blog?query=1212fv&sort=ACCURACY&page=1&size=10' \
  -H 'accept: */*'
}'
``` 
response
```
  "result": "SUCCESS",
  "data": {
    "contentsList": [
      {
        "title": "{제목}",
        "contents": "{내용}",
        "url": "{경로}",
        "blogname": "{블로그 제목}",
        "datetime": "{작성일시}"
      }
    ],
    "pagination": {
      "sort": "ACCURACY",
      "pageNumber": 1,
      "pageSize": 5,
      "totalElements": 10
    }
  }
}
``` 
#### 인기 검색어 목록 API
`GET /v1/search/blog/rank`

request
```
curl -X 'GET' \
  'http://localhost:8080/v1/search/blog/rank' \
  -H 'accept: */*'
```
response
```
{
  "result": "SUCCESS",
  "data": [
    {
      "keyword": "{검색어}",
      "count": {검색 횟수}
    }
  ]
}
```

## 사용 기술

- JDK 11
- Kotlin
    - Kotlin Coroutine
- Spring Boot
    - WebFlux
    - Data JPA
    - Docs Openapi(웹 테스트 확인용)
- Test
    - junit5
    - mockk
    - kluent
- Datasource
    - h2
    - Redis
        - redisson
          - 검색 랭킹 캐시용도로 사용
          - DB와 WRITE_BEHIND 동기화

##  프로젝트 구성


## 빌드 모듈 의존관계

- application
    - api : REST API 모듈
    - external-api-client : OPEN API 호출 모듈
- persistence
  - blog-search : DataBase 모듈

## Running Server

### 유의사항
M1의 경우 build.gradle.kts dependency 에 추가
(현재 추가 상태)
```
runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.77.Final:osx-aarch_64")
```

### Redis

서버 실행 후 Redis 확인
```
http://localhost:6379
```

### H2 console

서버 실행 후 Database 확인 콘솔
```
http://localhost:8888
```

### Swagger API 요청 테스트 경로

```
http://localhost:8080/swagger-ui.html
```

### 빌드 정보
```
./gradlew :application:api:clean :application:api:bootJar -x test 
```

### 빌드 결과물 다운로드 링크 정보
https://github.com/rihongtime/blog-search-api/blob/master/run/api-0.0.1-SNAPSHOT.jar

### Root에서 Terminal 서버 실행
```
java -jar run/api-0.0.1-SNAPSHOT.jar
```
