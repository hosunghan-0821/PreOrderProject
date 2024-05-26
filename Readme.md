# PreOrder Application

# Intro
- 위 어플리케이션은 성능측정 및 개선과 Assert 키워드를 활용한 코드품질 개선에 초점을 맞추어 개발하였습니다.
  -  성능측정 도구로는 Scouter와 Gatling을 사용하였습니다.
  - 데이터 접근 경계에서의 데이터 유효성 검증을 통해 프로그래밍 키워드인 assert를 활용하여 코드 품질을 고려하며 개발하였습니다.  

# 📅 기간 및 인원

- 개인프로젝트
- 2024/04 ~ 2024/06

# 📚 기술스택
- Backend
  - [ Java / SpringBoot / JPA / Ehcache / Redis]
- APM 및 부하 발생프로그램
  - [ Scouter / Gatling ]

# 🎬 서비스 설명
- 기존 운영하고 있는 빵 가게에서 수기로 예약을 작성하는 부분을 자동화 하고자 하는 아이디어에서부터 서비스를 기획하였습니다.
- 상품을 사전예약을 통해 예약하고 매장에서 픽업 해가는 서비스입니다.





# Application 구조 및  테스트 환경 구성

#### Application 구조

- 단일 WAS(Spring Boot Tomcat) + 단일 DB(PostgrSQL)로 구성하였습니다.

#### 테스트 환경

- 총 3대의 컴퓨터를 활용해서 환경을 구성하였습니다.
  - WAS(Spring Boot Tomcat) + Scouter(Agent,Host)
  - Gatling + Scouter(Client,Server)
  - DB +Scouter(Host)
  <br><br>

  <img width="691" alt="image" src="https://github.com/hosunghan-0821/PreOrderProject/assets/79980357/533e66e9-1b8f-4ac0-bfb2-dc666f4c9ecb">



# 기술적 고민 및 구현

1. Assert를 적극적으로 활용하여 함수의 선조건과 후조건을 검사함으로써, 불필요한 로직을 최소화 하도록 고민하면서 코딩하였습니다.<br>
   - 함수의 선조건, 후조건을 단언함으로써, 함수를 단순화하고 함수의 용도(스펙)을 명확하게 명시하도록 하였습니다.
     - 불필요한 if문이 사라지게 되었고, 개발 도중 assert에 걸릴 경우, 함수 호출자 쪽에서 문제를 해결하였습니다.
     - 위의 방식을 사용하니, 코드가 간결해지고 함수를 조금 더 명확하게 사용할 수 있게 되었습니다.<br><br>
2. 조회 API를 ehcache를 활용하여서 TPS를 개선하였습니다.
   - local cache인 ehcache를 선택한 이유
     - global cache인 사용할 경우, global cache를 운영하는 것에 대한 추가 공부가 필요하였습니다. 또한, 네트워크 비용이 추가적으로 들기 때문에 우선 로컬 캐시를 활용하기로 하였습니다.
     - 현재 프로젝트에서 global cache를 활용하는 것은 오퍼스펙이라고 판단하였습니다.
     - 추후에 scale out을 하더라도, ehcache에서 지원해주는 분산캐시 (Terracotta Server)를 통해서 캐시 동기화를 할 수 있을 것으로 판단하였습니다.<br><br>
3. 비관락 적용된 사전 예약 API - TPS & Response Time 개선 
   ### 개선안 1 : @Transactional 적용 범위 줄이기 + DB Hikari Connection Pool 증가
     - 기존의 @Transcational 코드에는 사전예약 후 사전예약 정보 insert 로직 + Notification 로직이 있었습니다. 이를 분리해서 @Transactional의 범위를 줄이고 테스트 하였습니다.<br><br>
     
     - 성능 내용 <br>
       - **@Transactional 적용 범위 줄이기**
         ![image](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/5d91f1aa-eb14-40b0-93a6-4a895b2633d0)
         ![image](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/dfaafd74-5e2c-4d1a-9603-8e6c3c37a160)
         - Transcational적용되는 범위를 줄였지만 여전히 TPS는 나오지 않고, 응답속도도 평균 3초가 넘는 문제 있는 API였습니다.
         - APM Xlog profiling을 확인하였을 때, connection을 획득하는 곳에서 많은 시간을 걸리는 것을 확인하였고, Hirari Connection Pool의 개수를 늘려서 테스트 하였습니다. <br><br>
       - **@Transcational 적용 범위 줄이기 + DB Hikari Connection Pool 증가시키기**
         ![short_transcation_browser](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/46e5866b-df1b-4ea1-9f0e-ba8fe98ec18c)
         ![select_for](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/109ce35d-a50d-4a7c-ba91-9fd7d438b921)
         -  APM Xlog profiling을 확인하였을 때, connection 획득하는 시간은 줄었지만, DB Lock을 획득하는데 추가적인 시간이 걸려서 결국 큰 차이가 없었습니다. <br><br>

    ### 개선안 2 : Ehcache에서 지원하는 Lock과 Local TranscationManager을 이용해 메모리단에서 Race Condition 해결하였습니다.
     
     - 개선안 1의 문제점을 분석하였을 때, DB Lock이 걸린 상태에서 (예약정보 Insert + DB Lock 대기) 비용이 크다는 것을 파악하였습니다. 
     - 이를 해결하기 위해, Ehcache에 DB Product 정보를 미리 Load시키고, 사전 예약 처리 후, 주기적으로 상품 개수의 변경점을 Write Back하는 방식을 채택하여 테스트 하였습니다.

     <br><br>
     - 성능 내용 <br><br>
       ![xlog](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/33830b54-ce3c-4664-bb7c-12a1608e3808)
       ![XLOG_PROFILING](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/60074861-82ac-435e-8780-3a7bc253155f)
       - 안정적인 TPS 와 Response Time이 확보되는 것을 알 수 있었습니다.
       - 다만, Ehcache에 여러 상품에 대해서 Lock 걸을 때에도, Race Condition이 작동해, DeadLock을 유발할 수 있는 문제점과 Applicaion이 갑자기 떨어지게 된다면, 그에 따른 남은 상품개수 정합성에 대한 문제점이 있었습니다.
       <br><br>

   ### 개선안 3 : Redis 도입과 Write Back 방식
     - global cache이면서, single thread로 동작하는 redis 기술을 도입하여, 작업의 원자성을 유지하도록 하여서 불필요한 락을 최소화하였습니다. 
     - 성능 내용 <br><br>
  

