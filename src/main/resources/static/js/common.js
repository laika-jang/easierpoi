setEvents();

function setEvents() {
    // 도로명 주소를 지번 주소로 변환
    document.getElementById('addr').addEventListener('change', changeAddr);

    // 유효성 검사
    document.getElementById('validity').addEventListener('click', validity);

    // 입력폼 초기화
    document.getElementById('initForm').addEventListener('click', initForm);
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
    const addrLoad = document.getElementById('addr').value === document.getElementById('addrNum').innerText ? '' : document.getElementById('addr').value;
    const addrNum = document.getElementById('addrNum').innerText;
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

    // msg 출력
    html += '<div id="result-msg" class="alert alert-secondary" role="alert">';
    html += data.msg;
    html += '</div>';

    // data.list가 없을 경우 함수 종료
    if (data.list === null) return false;

    // 검색 결과 출력
    html += '<div id="result-list" class="mt-4">';
    html += '<p>검색 결과</p>';
    html += '<ul class="list-group">';

    for (let i = 0; i < data.list.length; i++) {
        const mapId = `map-${i}`;

        html += '<li class="list-group-item">';
        html += '<p>' + data.list[i].place + ' <small class="text-body-tertiary">(' + data.list[i].category + ')</small></p>';
        html += '<small>' + data.list[i].addrLoad + '<br />';
        html += '<span class="text-body-tertiary">' + data.list[i].addrNum + '</span>';
        html += '</small>';
        if (data.status === "3") {
            html += `<div id="${mapId}" class="mt-2 text-center" style="max-width: 100%"></div>`;
            loadMapAsync(data.list[i], mapId);
        }
        html += '</li>';
    }

    html += '</ul>';
    html += '</div>';

    // 키워드 검색 결과 출력
    if (document.getElementById('place').value.includes(';')) {
        const keywordsList = document.getElementById('place').value.split(';');

        for (let i = 1; i < keywordsList.length; i++) {
            const keywords = keywordsList[i];

            html += '<div id="result-list" class="mt-5">';
            html += '<p>\'' + keywords + '\' 검색 결과</p>';
            html += '<ul class="list-group">';

            for (let j = 0; j < data[keywords].length; j++) {
                html += '<li class="list-group-item">';
                html += '<p>' + data[keywords][j].place + ' <small class="text-body-tertiary">(' + data[keywords][j].category + ')</small></p>';
                html += '<small>' + data[keywords][j].addrLoad + '<br />';
                html += '<span class="text-body-tertiary">' + data[keywords][j].addrNum + '</span>';
                html += '</small>';
                html += '</li>';
            }

            html += '</ul>';
            html += '</div>';
        }
    }

    document.getElementById('valid-result-container').innerHTML = html;
    document.getElementById('valid-result-container').classList.remove('d-none');
}

// 지도 이미지 생성
async function loadMapAsync(data, targetId) {
    const imgHtml = await getGeocode(data);
    const targetDiv = document.getElementById(targetId);

    if (targetDiv && imgHtml) targetDiv.innerHTML = imgHtml;
}

// 주소를 좌표로 변환
async function getGeocode(data) {
    const addr1 = document.getElementById('addrNum').innerText;
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

        return '<img src="' + imageObjectURL + '" class="mb-3" />';
    } catch (e) {
        console.error(e);
    }
}

// 폼 초기화
function initForm() {
    document.getElementById('place').value = '';
    document.getElementById('addr').value = '';
    document.getElementById('result-msg').innerHTML = '';
    document.getElementById('result-list').innerHTML = '';
    document.getElementById('result-container').classList.add('d-none');
}