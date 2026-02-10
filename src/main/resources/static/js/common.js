setEvents();

function setEvents() {
    // 유효성 검사
    document.getElementById('validity').addEventListener('click', validity);

    // 입력폼 초기화
    document.getElementById('initForm').addEventListener('click', initForm);

    // 테스트용 버튼
    document.getElementById('test01').addEventListener('click', test01);
    document.getElementById('test02').addEventListener('click', test02);
    document.getElementById('test03').addEventListener('click', test03);
    document.getElementById('test04').addEventListener('click', test04);
}

// 유효성 검사
async function validity() {
    const place = document.getElementById('place').value;
    const addrLoad = document.getElementById('addrLoad').value == null ? '' : document.getElementById('addrLoad').value;
    const addrNum = document.getElementById('addrNum').value == null ? '' : document.getElementById('addrNum').value;

    if (!place) return alert("상호를 입력하세요.");
    if (addrLoad === '' && addrNum === '') return alert("도로명주소 혹은 지번주소를 입력하세요.");

    try {
        const response = await fetch(`/api/v1/validity/get-result?place=${encodeURIComponent(place)}&addrLoad=${encodeURIComponent(addrLoad)}&addrNum=${encodeURIComponent(addrNum)}`);
        const data = await response.json();
        console.log(data);

        switch (data.result) {
            case 'find':
                document.getElementById('result-container').innerHTML = '<p>' + data.msg + '</p>';
                break;
            case 'similar':
                document.getElementById('result-container').innerHTML = '<p>' + data.msg + '</p>';
                break;
            case 'notFind':
                document.getElementById('keywords').style.display = 'block';
                document.getElementById('result-container').innerHTML = '<p>' + data.msg + '</p>';
                break;
            case 'error':
                document.getElementById('result-container').innerHTML = '<p>' + data.msg + '</p>';
                if (data.log !== null) document.getElementById('result-container').innerHTML += '<p>' + data.errorLog + '</p>';
                break;
        }
    } catch (e) {
        console.info(e);
    }
}

// 폼 초기화
function initForm() {
    document.getElementById('place').value = '';
    document.getElementById('addrLoad').value = '';
    document.getElementById('addrNum').value = '';
    document.getElementById('keywords').style.display = 'none';
    document.getElementById('result-container').innerHTML = '';
}

// 테스트
function test01() {
    // 같은 주소에 동일한 상호 있음
    document.getElementById('place').value = '조촌동 메가MGC커피 군산조촌점';
    document.getElementById('addrLoad').value = '전북 군산시 궁포3로 8';
    document.getElementById('addrNum').value = '전북 군산시 조촌동 2-72';
}
function test02() {
    // 다른 주소에 동일한 상호 있음
    document.getElementById('place').value = '조촌동 메가MGC커피 군산조촌점';
    document.getElementById('addrLoad').value = '전북 군산시 궁포3로 12';
    document.getElementById('addrNum').value = '전북 군산시 조촌동 2-37';
}
function test03() {
    // 동일한 상호를 찾지 못함
    document.getElementById('place').value = '조촌동 메가MGC커피 조촌점';
    document.getElementById('addrLoad').value = '전북 군산시 궁포3로 8';
    document.getElementById('addrNum').value = '전북 군산시 조촌동 2-72';
}
function test04() {
    // 도로명주소에 지번주소 입력
    document.getElementById('place').value = '조촌동 메가MGC커피 군산조촌점';
    document.getElementById('addrLoad').value = '전북 군산시 조촌동 조촌동 2-72';
    document.getElementById('addrNum').value = '전북 군산시 조촌동 2-72';
}