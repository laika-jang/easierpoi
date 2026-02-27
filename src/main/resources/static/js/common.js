setEvents();

function setEvents() {
    // 도로명 주소를 지번 주소로 변환
    document.getElementById('addr').addEventListener('change', changeAddr);

    // 유효성 검사
    document.getElementById('validate').addEventListener('click', validate);

    // 입력폼 초기화
    document.getElementById('init-form').addEventListener('click', initForm);
}

// 도로명 주소를 지번 주소로 변환
async function changeAddr() {
    const addr = document.getElementById('addr').value;
    const addrSplit = addr.split(' ');

    // 중복값 제거 및 주소 타입 구분
    let addrRemoveDupl = addrSplit[0];
    let isAddrLoad = false;

    for (let i = 1; i < addrSplit.length; i++) {
        if (addrSplit[i - 1] !== addrSplit[i]) addrRemoveDupl += ' ' + addrSplit[i];
        if (addrSplit[i].endsWith('로') || addrSplit[i].endsWith('길')) isAddrLoad = true;
    }

    // 주소 값이 도로명 주소인 경우 지번 주소로, 지번 주소인 경우 도로명 주소로 변환
    try {
        const response = await fetch(`/api/v1/validity/get-addr?addr=${encodeURIComponent(addr)}`);
        const data = await response.json();

        if (data.addrLoad !== undefined) document.getElementById('addr').value = data.addrLoad;
        document.getElementById('addr-num').innerHTML = data.addrNum !== undefined ? data.addrNum : isAddrLoad ? '지번주소 없음' : '도로명주소 없음';
    } catch (e) {
        console.error(e);
    }

    return isAddrLoad;
}

// 유효성 검사
async function validate() {
    if (!document.getElementById('place').value) return alert('상호를 입력하세요.');
    if (!document.getElementById('addr').value) return alert('주소를 입력하세요.');
    if (document.getElementById('place').value === document.getElementById('addr').value) return alert('입력값을 확인하세요.');
    if (document.getElementById('addr').value === 'null') return  alert('주소값이 없어 결과를 불러올 수 없습니다.');

    const isAddrLoad = await changeAddr();

    const place = document.getElementById('place').value;
    const addrLoad = isAddrLoad ? document.getElementById('addr').value : '';
    const addrNum = isAddrLoad ? document.getElementById('addr-num').innerText : document.getElementById('addr').value;
    const url = `/api/v1/validity/get-result?place=${encodeURIComponent(place)}&addrLoad=${encodeURIComponent(addrLoad)}&addrNum=${encodeURIComponent(addrNum)}`;

    try {
        const response = await fetch(url);
        const data = await response.json();

        drawValidityResult(data);
    } catch (e) {
        console.error(e);
    }
}

// 검사 결과 출력
function drawValidityResult(data) {
    let html = '';

    // data.list가 없을 경우 함수 종료
    if (data.list.length === 0) {
        html += '<div id="result-msg" class="alert alert-secondary" role="alert">';
        html += '없어요 (X)';
        html += '</div>';

        document.getElementById('result-container').innerHTML = html;
        document.getElementById('result-container').classList.remove('d-none');

        return false;
    }

    // 검색 결과가 있을 경우
    if (data.status === true) {
        html += '<div id="result-msg" class="alert alert-primary" role="alert">';
        html += '있어요 (O)';
        html += '</div>';
    }

    // 검색 결과 출력
    html += '<div id="result-list" class="mt-4">';
    html += '<p>검색 결과</p>';
    html += '<ul class="list-group">';

    for (let i = 0; i < data.list.length; i++) {
        const mapId = `map-${i}`;

        html += '<li class="list-group-item p-3">';

        switch (data.list[i].status) {
            case '1':
                html += '<span class="badge text-bg-primary me-2 mb-2">상호 일치</span>';
                html += '<span class="badge text-bg-success">주소 일치</span>';
                break;
            case '2':
                html += '<span class="badge text-bg-secondary me-2 mb-2">상호 불일치</span>';
                html += '<span class="badge text-bg-success">주소 일치</span>';
                break;
            case '3':
                html += '<span class="badge text-bg-primary me-2 mb-2">상호 일치</span>';
                html += '<span class="badge text-bg-secondary">주소 불일치</span>';
                break;
            case '4':
                html += '<span class="badge text-bg-secondary me-2 mb-2">상호 불일치</span>';
                html += '<span class="badge text-bg-secondary">주소 불일치</span>';
                break;
            default:
                break;
        }

        html += '<p>' + data.list[i].place + ' <small class="text-body-tertiary">(' + data.list[i].category + ')</small></p>';
        html += '<small>' + data.list[i].addrLoad + '<br />';
        html += '<span class="text-body-tertiary">' + data.list[i].addrNum + '</span>';
        html += '</small>';
        if (data.list[i].status === "3") {
            html += `<div id="${mapId}" class="mt-2 text-center" style="max-width: 100%"></div>`;
            loadMapAsync(data.list[i], mapId);
        }
        html += '</li>';
    }

    html += '</ul>';
    html += '</div>';

    document.getElementById('result-container').innerHTML = html;
    document.getElementById('result-container').classList.remove('d-none');
}

// 지도 이미지 생성
async function loadMapAsync(data, targetId) {
    const imgHtml = await getGeocode(data);
    const targetDiv = document.getElementById(targetId);

    if (targetDiv && imgHtml) targetDiv.innerHTML = imgHtml;
}

// 주소를 좌표로 변환
async function getGeocode(data) {
    const addr1 = document.getElementById('addr-num').innerText;
    const url = `/api/v1/validity/get-code?query=${encodeURIComponent(addr1)}`;

    try {
        const response = await fetch(url);
        const result = await response.json();
        const param = {
            'addr1x': result.addresses[0].x,
            'addr1y': result.addresses[0].y,
            'addr2x': (data.mapx/10000000).toString(),
            'addr2y': (data.mapy/10000000).toString()
        };

        return getMapImg(param);
    } catch (e) {
        console.error(e);
    }
}

// 지도 이미지 생성
async function getMapImg(param) {
    const url = `/api/v1/validity/get-img?addr1x=${encodeURIComponent(param.addr1x)}&addr1y=${encodeURIComponent(param.addr1y)}&addr2x=${encodeURIComponent(param.addr2x)}&addr2y=${encodeURIComponent(param.addr2y)}`;

    try {
        const response = await fetch(url);
        const result = await response.blob();
        const imageObjectURL = URL.createObjectURL(result);

        return '<img src="' + imageObjectURL + '" class="mb-2" />';
    } catch (e) {
        console.error(e);
    }
}

// 폼 초기화
function initForm() {
    document.getElementById('place').value = '';
    document.getElementById('addr').value = '';
    document.getElementById('addr-num').innerHTML = '';
    document.getElementById('result-container').innerHTML = '';
    document.getElementById('result-container').classList.add('d-none');
}