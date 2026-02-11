setEvents();

function setEvents() {
    // 도로명 주소를 지번 주소로 변환
    document.getElementById('addr').addEventListener('change', changeAddr);

    // 유효성 검사
    document.getElementById('validity').addEventListener('click', validity);

    // 입력폼 초기화
    document.getElementById('initForm').addEventListener('click', initForm);

    // 테스트용 버튼
    document.getElementById('test').addEventListener('change', test);
}

// 도로명 주소를 지번 주소로 변환
async function changeAddr(e) {
    const addr = document.getElementById('addr').value;
    const addrSplit = addr.split(' ');

    if (addrSplit[2].endsWith("로") || addrSplit[2].endsWith("길") || addrSplit[3].endsWith("로") || addrSplit[3].endsWith("길")) {
        // 주소 값이 도로명 주소인 경우 지번 주소로 변환
        try {
            const response = await fetch(`/api/v1/validity/get-addr-num?addr=${encodeURIComponent(addr)}`);
            const data = await response.text();

            document.getElementById('addrNum').innerHTML = data;
        } catch (e) {
            console.error(e);
        }
    } else {
        // 주소 값이 지번 주소인 경우에는 변환하지 않음
        // 중복값 제거
        let addrLoad = addrSplit[0];

        for (let i = 1; i < addrSplit.length; i++) {
            if (addrSplit[i - 1] !== addrSplit[i]) addrLoad += ' ' + addrSplit[i];
        }
        document.getElementById('addr').value = addrLoad;
        document.getElementById('addrNum').innerHTML = addrLoad;
    }
}

// 유효성 검사
async function validity() {
    if (!document.getElementById('place').value) return alert("상호를 입력하세요.");
    if (!document.getElementById('addr').value) return alert("주소를 입력하세요.");

    await changeAddr();

    const place = document.getElementById('place').value;
    const addrLoad = document.getElementById('addr').value === document.getElementById('addrNum').innerHTML ? '' : document.getElementById('addr').value;
    const addrNum = document.getElementById('addrNum').innerHTML;

    try {
        const response = await fetch(`/api/v1/validity/get-result?place=${encodeURIComponent(place)}&addrLoad=${encodeURIComponent(addrLoad)}&addrNum=${encodeURIComponent(addrNum)}`);
        const data = await response.json();
        console.log(data);
    } catch (e) {
        console.error(e);
    }
}

// 폼 초기화
function initForm() {
    document.getElementById('place').value = '';
    document.getElementById('addr').value = '';
    document.getElementById('result-container').innerHTML = '';
}

// 테스트
function test(e) {
    switch (e.target.value) {
        case '1':
            // 같은 주소에 동일한 상호 있음
            document.getElementById('place').value = '조촌동 메가MGC커피 군산조촌점';
            document.getElementById('addr').value = '전북특별자치도 군산시 궁포3로 8';
            break;
        case '2':
            // 동일한 상호를 찾지 못함
            document.getElementById('place').value = '구로동 메가MGC커피 조촌점';
            document.getElementById('addr').value = '전북특별자치도 군산시 궁포3로 8';
            break;
        case '3':
            // 다른 주소에 동일한 상호 있음
            document.getElementById('place').value = '조촌동 메가MGC커피 군산조촌점';
            document.getElementById('addr').value = '전북특별자치도 군산시 궁포3로 12';
            break;
        default:
            document.getElementById('place').value = '';
            document.getElementById('addr').value = '';
            document.getElementById('result-container').innerHTML = '';
            break;
    }
}