# [🌹 틸리 - 꾸준하고픈 개발자를 위한 공간](https://kc29be941feb6a.user-app.krampoline.com/)

<p align='center'>
<img width="200" alt="스크린샷 2023-03-16 오전 9 30 09" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/908cde6b-1f19-4a35-bc36-7c77309ffef1">
</p>

<p align='center'>
    <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
    <img src="https://img.shields.io/badge/Amazon_AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white">
    <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white">
    <img src="https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white">
    <img src="https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white">
</p>

<br/>

# 🔗 관련 주소

| 문서 | 
|:--------:|
| [7조 배포 주소](https://k50d31201bb8ea.user-app.krampoline.com) |
| [API 문서](https://www.notion.so/API-d7c21dd77c1643348c98b01c8f3d9f2a) |
| [피그마](https://www.figma.com/file/CBibyBNZ1jmESyVs0jnjSt/3%EB%8B%A8%EA%B3%84-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%99%80%EC%9D%B4%EC%96%B4-%ED%94%84%EB%A0%88%EC%9E%84?type=design&node-id=0-1&mode=design&t=0h0155bB1sb2wp98-0) |
| [7조 노션](https://www.notion.so/2a6af605e8184499b21492cb7aabf6f5?v=0b907fed27634982ace606d37a4a6c88) |


# 🏳️‍🌈 목차
1. [서비스 기획 의도](https://github.com/suuding/Team7_BE_TILy?tab=readme-ov-file#-%EC%99%9C-%EC%9D%B4%EB%9F%B0-%EC%84%9C%EB%B9%84%EC%8A%A4%EB%A5%BC)
2. [주요 기능](https://github.com/suuding/Team7_BE_TILy?tab=readme-ov-file#-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5)
3. [BE - 핵심 개발 영역](https://github.com/suuding/Team7_BE_TILy?tab=readme-ov-file#-be---%ED%95%B5%EC%8B%AC-%EA%B0%9C%EB%B0%9C-%EC%98%81%EC%97%AD)
4. [ERD](https://github.com/suuding/Team7_BE_TILy?tab=readme-ov-file#-erd)
5. [아키텍쳐 구조](https://github.com/suuding/Team7_BE_TILy?tab=readme-ov-file#-erd)
6. [TIL-y 구성원](https://github.com/suuding/Team7_BE_TILy?tab=readme-ov-file#-til-y-%EA%B5%AC%EC%84%B1%EC%9B%90)


<br/>
<br/>

# 🤔 왜 이런 서비스를?

## 📍 문제 상황 인식 1단계 <카테캠 1,2 단계를 겪으며>
- 카테캠의 핵심, 자기주도적 학습 -> **매일, 매주 TIL 작성 및 제출**
- 하지만 100명이 넘는 학생들의 TIL을 **노션의 한 페이지에서 관리**
<p align='center'>
    <img width="400" alt="image" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/fa0355af-0242-472d-b0e6-0f313edd7a89">
    <img width="350" alt="image" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/45d8a088-7119-49f8-a311-cdd87d0acad5">
</p>

```
학생의 불편함 - 내 TIL이 삭제되는 일, 서로의 모든 TIL이 공개
멘토의 불편함 - 일일히 들어가서 작성 여부를 확인, 제출 여부 확인 어려움

-> 학습일지를 더 편하게 관리하고 제출할 수는 없을까?
```

### ⭐️ 문제 해결 방안 <그룹 로드맵 서비스>
- 로드맵을 만들어서 구성원들을 가입할 수 있게 하자
- 로드맵의 각 단계를 직접 생성하고, 구성원들은 각 단계별로 학습할 수 있도록 하자
- 제출 기한에 맞춰 단계별로 학습한 TIL을 제출할 수 있도록 하자
- 한 눈에 제출된 TIL들을 확인할 수 있게 하자

<hr/>

## 📍 문제 상황 인식 2단계 <자기주도적 개발 학습의 어려움>
- 개발, 스택 공부는 스스로 시작해야하는 경우가 많음
```
하지만 어디서부터 어떤 순서로 해야할지 막막함
내가 잘하고 있는지도 확인하기 어려움

-> 어떤 순서로 공부할지, 또 잘하고 있는 지 확인할 방법은 없을까?
```
### ⭐️ 문제 해결 방안 <로드맵 제공 및 TIL 공유>
- 스스로 학습할 수 있게 로드맵들을 제공하자
- 각 단계별 참고 자료와 함께 학습하고 제출할 수 있도록 하자
- 제출이 완료되면 해당 단계에 대해서 제출된 TIL들을 볼 수 있도록 하자.
- 다른 사람들의 TIL을 보며 자신이 잘 학습했는지 확인할 수 있도록 하자.
 

<p align='center'>
<img width="3000" alt="스크린샷 2023-03-16 오전 9 30 09" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/0eb148fe-0d60-4b33-918e-546fdab21e69">
</p>

<br/>
<br/>


# 🧩 주요 기능
|TIL 작성|학습 참고|
|:--:|:--:|
|- 마크다운 에디터를 통한 TIL 작성<br/>-사라질 걱정 없는 상시 저장 기능<br/> |- 각 STEP별 참고자료 확인<br/>- 글에 대한 코멘트 확인|
|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/0891a195-b7d3-4cf5-8d83-66f15e1ce695">|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/7f9dd89d-d40d-415b-9c2b-f63363ced835">|

|메인|참고 자료|
|:--:|:--:|
|- 작성한 TIL 목록들을 검색하고 확인<br/>- 장미밭을 통해 학습 열정 확인 <br/> - 개인, 그룹 로드맵을 분류하여 관리|- 로드맵에 참고할 자료를 첨부하는 기능<br/>-유튜브, 참고자료 링크<br/> |
|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/101dc410-2f68-4ca4-84b6-c5d00005df84">|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/f0812e9f-f1df-4c94-bbb0-a0dab46f022f">|

|로드맵 목록|구성원 관리|
|:--:|:--:|
|- 내가 참여하고 있는 로드맵의 목록을 확인<br/>- 현재 모집중인 그룹 로드맵 목록 확인|- 현재 로드맵에 속한 그룹원 목록<br/>-멤버 권한 변경, 강퇴 기능<br/> -그룹원의 학습일지 작성현황 확인<br/>  -로드맵 신청 관리, 수락 거절<br/>|
|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/0cabf489-e6e4-4236-aba1-b8d78b18f316">|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/a975f794-b7b1-40db-be2f-51890e4d9d39">|

|TIL 공유하기|깃허브 업로드|
|:--:|:--:|
|- 내가 공부하는 주제에 대해 타인과 생각을 공유<br/>|- 작성한 학습일지를 깃허브에 업로드 하는 기능<br/>
|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/db9467c1-8759-49d7-9f10-1ddda8744ea3">|<img width="350" alt="스크린샷 2023-03-19 오후 11 51 04" src="https://github.com/monsta-zo/Team7_FE/assets/83194164/735e6859-ff34-49f9-a797-930fddaecd04">|


<br/>
<br/>


# 🎯 BE - 핵심 개발 영역


<table>
  <tr>
    <th>기능</th>
    <th>설명</th>
  </tr>
  <tr>
    <td>로그인/회원가입</td>
    <td> gmail(smtp)와 redis를 사용하여 이메일 인증 시스템을 구현했지만, 배포환경 변화로 인해 크램폴린에 배포하지는 못함 </br> 로그인과 재발급 요청에 따른 access token, refresh token 발급을 통해 JWT 관리 </td>
  </tr>
  <tr>
    <td> 검색 기능 구현 </td>
    <td> 메인 페이지에서 TIL 제목으로 검색, 장미밭 클릭으로 특정 날짜의 TIL 조회, 로드맵 페이지에서 로드맵 이름으로 검색, 상세 페이지에서 구성원 이름으로 검색 기능 구현 </td>
  </tr>
  <tr>
    <td> 이미지 업로드 </td>
    <td> S3 구축과 연동을 통해 프로필 사진 업로드, TIL 사진 업로드, 삭제 기능 구현 (크램폴린에서 연동하지 못함) </td>
  </tr>
  <tr>
    <td> 알림 기능 구현 </td>
    <td> 자신이 제출한 TIL에 달린 댓글에 대한 알림 기능 구현 </td>
  </tr>
  <tr>
    <td> 댓글 기능 구현 </td>
    <td> 제출한 TIL에 댓글 작성과 수정, 삭제 기능 구현 </td>
  </tr>
  <tr>
    <td> 삭제 기능 구현 </td>
    <td> 삭제 기능을 Soft delete로 구현하여, 데이터 무결성을 유지하고, 삭제된 데이터의 이력을 추적하거나 데이터 분석에 활용할 수 있도록 함. </br> JPQL과 @Where을 이용하여 실제로 데이터가 삭제되지 않고 삭제된 것처럼 보이도록 구현. </td>
  </tr>
  <tr>
    <td> 예외 처리 </td>
    <td> 예외 코드를 ExceptionCode로 일괄 관리를 통해 유지 보수성와 중복 최소화 함 </td>
  </tr>
  <tr>
    <td> 권한 처리 </td>
    <td> 로드맵에서의 사용자의 역할(master, manager, member, none)에 맞는 권한 처리 </td>
  </tr>
  <tr>
    <td> 테스트 코드 작성  </td>
    <td> Junit을 이용하여 컨트룰러 단위 테스트를 진행하며 프로덕션 환경에서의 안전성 확보함 </br> </td>
  </tr>
  <tr>
    <td> 성능 개선 </td>
    <td> lazy fetching 전략과 fetch join 사용으로 N+1 문제 개선 및 쿼리 효율성을 증가시킴 </br> 또한 엔티티 간 양방향 연관 관계가 아닌 단방향 연관 관계 설정을 통해 불필요한 참조와 의존성을 줄임 </td>
  </tr>
  <tr>
    <td> 가독성 및 리팩토링 </td>
    <td> DTO를 class가 아닌 record로 구현해 depth를 줄이고 재사용성과 가독성을 높임 </td>
  </tr>
</table>

</br>
</br>


# 🏠 ERD
![TIL-y_ERD](https://github.com/Step3-kakao-tech-campus/Team7_BE/assets/95485737/e3db0e8f-ec7b-4048-8c6e-a628722776ea)

<br/>
<br/>

# ⚙️ 아키택쳐 구조
<img width="800" alt="스크린샷 2023-11-11 오후 8 02 21" src="https://github.com/Step3-kakao-tech-campus/Team7_BE/assets/131665728/aff5dd73-0cc2-4da4-8e53-f838630b7afd">
</br>
</br>



# 👨‍💻 TIL-y 구성원

<table>
  <tr>
    <td>김동영</td>
    <td>조준서</td>
    <td>이한홍</td>
    <td>김수현</td>
    <td>이상명</td>
  </tr>
  <tr>
    <td><img src="https://github.com/ehddud1006.png" alt="김동영" width="100" height="100"></td>
    <td><img src="https://github.com/monsta-zo.png" alt="조준서" width="100" height="100"></td>
    <td><img src="https://github.com/hoyaii.png" alt="이한홍" width="100" height="100"></td>
    <td><img src="https://github.com/suuding.png" alt="김수현" width="100" height="100"></td>
    <td><img src="https://github.com/sam-mae.png" alt="이상명" width="100" height="100"></td>
  </tr>
  <tr>
    <td>FE</td>
    <td>FE</td>
    <td>BE</td>
    <td>BE</td>
    <td>BE</td>
  </tr>
  <tr>
    <td>조장</td>
    <td>기확리더</td>
    <td>테크리더</td>
    <td>스케줄러</td>
    <td>리마인더</td>
  </tr>
</table>
</br>


