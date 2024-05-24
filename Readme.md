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
   관련 글 링크 달기 -> (어떤점이 좋고, 코딩할 때 유의해야할 점 기타 등등..)
2. 조회 API를 ehcache를 활용하여서 TPS를 개선하였습니다.
   - local 캐쉬를 선택한 이유 + 성능개선 결과
3. 비관락 적용된 사전 예약 API TPS 개선
   - 개선안 1 : @Transactional 범위 줄이기 + DB Hikari Connection Pool 증가
     - 성능 내용
   - 개선안 2 : Ehcache의 Lock과 Local TranscationManager을 이용해 메모리단에서 Race Condition 해결
     - 성능 내용
   - 개선안 3 :
     - 성능 내용
   - 최종 
4. 인덱싱 처리를 하여서 예약 조회 성능 개선하였습니다.
