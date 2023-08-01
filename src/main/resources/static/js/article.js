//삭제 기능
const deleteButton = document.getElementById('delete-btn');

if(deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, { //fetch(url, {}) : 첫번째 인자로 받은 url + 두번째 인자로 받은 HTTP 메서드로 요청을 보냄
            method: 'DELETE'
        })
        .then(() => {
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        });
    });
}

// 수정 기능
// id가 modify-btn인 엘리먼트 조회
const modifyButton = document.getElementById('modify-btn');

if(modifyButton) {
    //클릭 이벤트가 감지되면 수정 API 요청
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search); //인스턴스 생성
        let id = params.get('id'); // 인스턴스에 get()을 이용하여 매개변수로 전달받은 값의 첫번째 값을 가져옴

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json", //요청 형식 지정
            },
            body: JSON.stringify({ // 입력한 데이터를 JSON 형식으로 바꿔서 보냄
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
        .then(() => {
            alert('수정이 완료되었습니다.');
            location.replace(`/articles/${id}`);
        })
    });
}

// 등록 기능
//id가 create-btn인 엘리먼트
const createButton = document.getElementById("create-btn");

if(createButton) {
    // 클릭 이벤트가 감지되면 생성 API요청
    createButton.addEventListener("click", (event) => {
        fetch("/api/articles", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value,
            }),
        }).then(() => {
            alert("등록 완료되었습니다.");
            location.replace("/articles");
        });
    });

}