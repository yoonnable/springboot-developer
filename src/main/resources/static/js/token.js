// 파라미터로 받은 토큰이 있다면 토큰을 로컬 스토리지에 저장한다.
const token = searchParam('token')

if(token) {
    localStorage.setItem("access_token", token) // 로컬 스토리지에 토큰 저장
}

function searchParam(key) {
    return new URLSearchParams(location.search).get(key); // local Storage에 저장할 토큰 스프링 부트 애플리케이션으로부터 전달받자.
}