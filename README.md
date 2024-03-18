# be-was-2024
코드스쿼드 백엔드 교육용 WAS 2024 개정판
## 사전안내

OOP

- 초기 패키지 외에 최대한 의존성 줄이기.
- NIO, Lombok 사용금지
- MVC 관련 네이밍 금지

---
### Step - 1

- [x] 요청을 받아 한번 html 파일을 보내보자.
    - 요청 라인을 공백으로 구분했을 때, 2번째 요소인 경로를 구한다.
    - 이 경로를 통해 html 파일을 읽는다.
    - 이 읽은 내용을 응답 메시지의 Body에 넣어 보낸다.

- [x] 이미지 파일이 깨진다. 해결해보자.
    - 현재 response msg를 보낼 때의 Content-Type이 text/html로 고정되어있다.
    - 그래서 이미지 파일의 내용을 보내도 해석하지 못하고 엑박으로 출력되는 것이다.
- [x] 추가로, 필요한 확장자들에 대한 대응을 해보자.
    - 확장자들을 enum으로 묶어 관리하도록 했다. (현재 html, css, svg, ico의 요청이 있다.)

- [ ] 유효하지 않은 파일에 대한 요청의 응답?
    - 400 Bad Request, 404 File Not Found 등이 있는데, 추후에 구현하도록 하자.

---

## Step - 2

- localhost:8080/registration 이라는 요청을 받았을 때, 어떻게 처리할까?
    - parsePath 를 사용하면 확장자 없는 경로가 된다.
        - 추가로 localhost:8080/registration/ 처럼 `/` 로 끝나는 경로에도 처리해야 한다.
    - 위와 같은 경우에 대해 registration/index.html의 정보를 넘겨주도록.

- 웹페이지에서 어떻게 서버로 데이터를 보낼 수 있을까?
    - HTML의 form, input을 이용해서 사용자의 데이터를 받고, type이 submit인 button으로 전송한다.
    - 이 때, HTTP 요청 메소드는 form에서 설정한 메소드로 설정된다.
- 결과
> `GET /create?id=12&name=34&password=56 HTTP/1.1`

- Method가 GET인 경우 위처럼 Path에 정보가 담기고, POST인 경우에는 Body 부분에 정보가 담긴다.

- 브라우저에서 전달된 Path를 파싱
    - ?를 기준으로 나눠 앞을 실행부, 뒤를 데이터 로 나눈다.
        - 실행부 : /user/create 같은 부분으로 어떤 메소드를 실행할지 나눈다.
        - 데이터 : HTML input의 name 필드가 고유한 값을 갖는다고 생각해서 HashMap을 사용하려고 했다. 그런데 알고보니 name은 `중복이 가능한` 필드였다.

- Path
    - ?가 없다
        - 지금까지의 처리
    - ?가 있다!
        - ? 앞에 대해 tmpName
        - ? 뒤에 대해 query 로 저장해둔다.

- 일반적인 파일요청 // create?name=test 같이 데이터를 담은 요청? 어떻게 구분할까?
    - 처음에는 데이터가 존재하는 경우에 따라 구분을 하려고 했다. 그런데, 네이버의 경우에서 `https://shopping.naver.com/home?dasdasd` 같이 넣어줘도 정상? 동작하는 것을 볼 수 있었다.
    - 그래서 모든 판단은 쿼리데이터를 제외하고 앞의 path만으로 판단해서 동작하면 될 것 같다.

- HttpRequest 클래스
    - 전송받은 Http 요청의 시작라인, 헤더, 바디를 나눠서 필드로 갖고 사용할 수 있는 클래스.

---
## 흐름

Http 요청 도착 - 특수한 경우(user/create) 들에 대해 처리를 해준다.
- 나머지의 경우는 요청한 Path의 파일을 읽어온다.

- Http 응답 작성 - 각각의 처리 메소드에서 status code, Header 그리고 필요하다면 Body까지 중 필요한 것을 response를 생성해서 보낸다.

responseMaker
- 정상흐름에서 200 코드를 반환하고 해당 페이지에 대한 정보가 body로서 반환될때. code, header, body
- 정상흐름에서 307 같은 리다이렉션 코드를 반환하고 location이 반환되는 경우. code, header + location(이것 자체도 헤더...)
- 그리고 경로가 잘못된 경우 404 코드를 반환한다.

HttpResponse 클래스?




---
# 문제
회원가입 시 form에서 action을 /user/create로 주었다. 그리고 이 path를 가진 요청이 들어왔을때 쿼리를 가지고 addNewUser를 실행한 뒤, 즉시 200response와 함께 /index.html을 바디로 담아 보냈다.

그런데, 2가지 문제가 발생했습니다. 먼저,

```
18:43:26.885 [DEBUG] [pool-1-thread-6] [webserver.RequestHandler] - Request : GET /user/reset.css HTTP/1.1
18:43:26.885 [DEBUG] [pool-1-thread-2] [webserver.RequestHandler] - Request : GET /user/img/signiture.svg HTTP/1.1
18:43:26.885 [DEBUG] [pool-1-thread-7] [webserver.RequestHandler] - Request : GET /user/img/like.svg HTTP/1.1
18:43:26.885 [DEBUG] [pool-1-thread-8] [webserver.RequestHandler] - Request : GET /user/img/sendLink.svg HTTP/1.1
18:43:26.885 [ERROR] [pool-1-thread-2] [webserver.RequestHandler] - file not found : ./src/main/resources/static/user/img/signiture.svg
18:43:26.885 [ERROR] [pool-1-thread-7] [webserver.RequestHandler] - file not found : ./src/main/resources/static/user/img/like.svg
18:43:26.885 [ERROR] [pool-1-thread-6] [webserver.RequestHandler] - file not found : ./src/main/resources/static/user/reset.css
18:43:26.885 [ERROR] [pool-1-thread-8] [webserver.RequestHandler] - file not found : ./src/main/resources/static/user/img/sendLink.svg
```

위와 같이 index.html에 필요한 css, svg 파일의 요청이 /user/ 경로를 붙여 들어와서 존재하지 않는 파일을 참조하게 되었습니다.

두번째는 아래에 보이듯 입력한 내용이 주소창에 그대로 유지되었다는 점입니다.

> http://localhost:8080/user/create?userid=1&name=2&password=3&email=4%401

두가지 문제를 겪었는데, 이는 303 See Other 응답을 보내서 메인페이지로 리다이렉트하도록 했습니다.


---
이후 추가할 것
Response Code;
- 숫자로는 상수의 이름을 정할 수 없는데 어떻게 하지?
    - 상태메시지를 간단히 요약한 문자열을 통해 사용하는게 낫겠다. 정해진 응답코드라고는 하지만 코드 상에서는 같은 숫자이기 때문에.

HttpResponse class;



---
왜 HTTP 메시지는 바이트 포맷으로 보내는가?
파일 내용을 바이트 형식으로 보내는 이유는 다음과 같습니다:

호환성: 바이트 형식은 거의 모든 컴퓨터 시스템에서 이해할 수 있습니다. 문자, 이미지, 오디오, 비디오 등 다양한 유형의 데이터를 바이트 형식으로 변환하여 보낼 수 있기 때문에, 여러 환경에서 파일을 쉽게 전송하고 읽을 수 있습니다.

정확성과 무결성: 바이트 형식으로 데이터를 전송하면, 데이터가 손상되지 않고 정확하게 전w송될 가능성이 높아집니다. 텍스트 기반의 데이터 전송은 인코딩 문제로 인해 데이터가 올바르게 전송되지 않을 위험이 있습니다.

효율성: 바이트 형식은 데이터를 압축하고 최적화하는 데 유리합니다. 이로 인해 파일 전송 시간이 단축되고 대역폭 사용이 줄어들 수 있습니다.

따라서 HTTP 응답에서 데이터를 바이트 형식으로 전송하는 것은 데이터의 호환성, 정확성, 효율성을 보장하기 위한 중요한 방법입니다.


Response 303 Code : 303 See Other: 서버가 사용자의 GET 요청을 처리하여 다른 URL에서 요청된 정보를 가져올 수 있도록 응답하는 코드.


---
## Feedback

> HttpRequest를 생성할 때 꼭 InputStream에서 해야하는 것인가 고려해보세요 물론 파일이든 네트워크든 InputStream 중에 하나에서 생길 가능성이 높긴한데 그걸 HttpRequest 에서 의존해서 읽고 처리해야 하는가도 고민해보세요 예를 들어 RequestLine, Header, Body 만 넘겨주도록 만들면 Request는 훨씬 가볍고 하위 모델에 가까울 겁니다. 여러 패키지에 의존하도록 만드는 게 좋은 건 아니거든요
- RequestParser 생성, 다른 형식으로 Http 요청이 들어올 때 해당 처리를 추가하려고 합니다.

> RequestLine 처럼 ResponseLine도 만들어보면 어떨까요?
 - 생성

> 이런 식으로 생각해보세요. httpRequest에서 값을 가져와서 "/user/create" 랑 비교하기 보다는 request에서 미리 정의된 종류인지 아닌지 비교하는 메소드를 구현해보는 것도 고려해보세요
- actionMap 에 Path와 동작메소드를 묶어서 작성했습니다.

> addHeader 나 body 부분도 RequestHandler보다 HttpResponse 같은 하위 타입을 선언하고 거기서 구현해보는 것도 좋습니다.

> 테스트가 구체적이라 좋습니다
다만 값을 확인하는 과정이 조금 복잡한 것처럼 느껴지긴 하네요.
헤더를 가져올 때도 headers를 다 가져오는 게 아니라 Content-Length만 가져올 수 있어도 좋을 것 같습니다.
- 현 시점, 아직 Header에 대한 getter가 필요하지는 않습니다. 하지만, getHeader 를 추가하는 것이 충분히 합리적이라고 생각해서 getHeader를 추가하고 테스트에 사용했습니다.

---
## Step - 3

JPG, PNG, JS 확장자를 추가한 뒤, 그림파일과 JS 이 제대로 로딩되는지 테스트를 진행했습니다. 그런데, 그림 파일이 제대로 로딩되지 않았습니다. 

확인해보니 응답 시작라인에도 문제가 없고, 헤더에도 Content-Type, Content-Length 이 잘 포함됨을 확인했습니다.

그리고 이전에 HTML이나 SVG 그림파일이 잘 동작했으니 바디에도 이상이 없을거라 생각하고 늪에 빠졌는데....

데이터를 가져오는 방식을 `BufferedReader br = new BufferedReader(new FileReader(path)); String line = br.readline();` 의 형태로 사용하고 있었습니다.

이미지파일은 `바이트 데이터`로 이뤄져 있는데, 이 데이터를 읽어 문자열로 변환하는 과정에서 손상이 온 것이었습니다.

FileInputStream.readAllBytes() 을 사용해서.


---

주소처리하는 핸들러, 회원가입하는 핸들러.

---

Step - 4

main - run - 메소드확인
- get - path(target) 확인 - 해당파일 내용반환
- post - path(target) 확인 - path(target)에 해당하는 동작.


GET인지 POST인지 요청메소드를 확인하고, 각각의 Target에 따라 진행한다. 

---
What To do?

### Code

body 를 byte[] 형태로 변경.
- 이후 문자열 처리에서는 new String(body, "UTF-8") 의 형태로 사용하려고 합니다.

HTTP 요청의 Tartget이 폴더의 형태로 끝날 경우 .index를 붙여 path 필드에 저장하던 것(parsePathWithIndex)을 삭제하고, Tartget 그대로 저장.
- 이 수정사항이 적용되면, HTTP 요청이 GET 메소드이고 Target이 폴더일때에 대한 처리가 추가되어야 한다

POST로 들어오는 회원가입 요청 처리.
- Path에서 데이터를 가져오던 기존 방식에서, 요청의 body에서 데이터를 가져오도록 변경해야 한다.



### HTML

회원가입의 GET 요청을 POST로 변경.
- 메소드만 바꾸는 것이 아니라, target 에 담기던 정보가 body로 옮겨지도록 해야한다.

---
문제였던것

- GET 메소드의 Target으로 폴더가 들어온 경우 파일을 읽기 전에 경로에 index.html 를 붙이는 수정을 했습니다. path를 변경하는 흐름은 이전에 사용하던 그대로 가져왔는데, 몇번을 생각해도 되어야 할 것이 제대로 동작하지 않았습니다.

![img.png](https://github.com/imojaek/img/assets/152826980/ce943555-8102-4ce2-aea0-4a6ca2b07ca7)

이유는 사진처럼 BASE_PATH를 상대경로로 지정해서 .이 포함되어있었는데, 확장자를 구분하는 '.' 만 생각했기 때문이었습니다.

- 307을 사용하던 리다이렉션을 302로 변경했습니다. 302, 307 두 응답코드 모두 일시적인 리다이렉션을 나타내지만 요청메소드가 GET으로 변하는가, 변하지않는가 의 차이였습니다.

GET만 사용할 때는 몰랐는데, 이번에 POST를 사용하면서 `단순 리다이렉션`에는 302를 써서 GET 요청을 통해 페이지를 불러오는 것이 맞다는 것을 알았습니다.

- 응답코드 301과 302의 차이를 알았습니다. 

응답코드에 대해 처음 공부할때는 `영구적`, `일시적` 이라는 키워드가 잘 감이 오지 않았습니다. 쿠키와 캐시를 공부하다가 알게 되었는데 영구적 리다이렉션인 301 코드를 응답받은 브라우저는 이를 캐시에 저장해뒀다가, 이후 해당 url에 대한 요청을 새 URL로 자동적으로 리턴합니다.

302 응답코드를 통한 리다이렉션은 `일시적`이므로 캐싱을 하지 않고, 이후 같은 url에 대한 요청을 그대로 서버에 다시 요청합니다.
