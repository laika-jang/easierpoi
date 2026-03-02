// bootstrap3 관련 변수
const dataModal = new bootstrap.Modal('#dataModal');
const dataModalElem = document.getElementById('dataModal');

setCoordEvents();

function setCoordEvents() {
    // 구글시트 데이터 불러오기
    document.getElementById('get-coord-data').addEventListener('click', coordCorr);

    // 입력폼 초기화
    document.getElementById('init-data').addEventListener('click', initCoordData);
    document.getElementById('tab').addEventListener('click', initCoordData);
}

// 구글시트 데이터 불러오기
async function coordCorr() {
    const url = `/api/v1/coord-corr/get-data`;

    try {
        const response = await fetch(url);
        const data = await response.json();

        // 시트 데이터 출력
        drawCoordResult(data);
    } catch (e) {
        console.error(e);
    }
}

// 시트 데이터 출력
function drawCoordResult(data) {
    let html = '';

    // data.list가 없을 경우
    if (data.length === 0) {
        html += '<tr>';
        html += '<td rowspan="5" class="text-center p-3">';
        html += '데이터가 없습니다. 파일을 확인하세요.';
        html += '</td>';
        html += '</tr>';

        document.querySelector('#data-container .table tbody').innerHTML = html;
        document.getElementById('data-container').classList.remove('d-none');

        return false;
    }

    // 검색 결과 출력
    for (let i = 0; i < data.length; i++) {
        const ticketID = data[i].localProfileID;

        html += '<tr data-bs-toggle="modal" data-bs-target="#dataModal" data-bs-idx="' + i + '">';
        html += '<th scope="row" style="cursor: pointer">' + ticketID + '</th>';

        if (data[i].truncatedAddr === '행의 일부 값이 올바르지 않습니다.') {
            html += '<td colspan="4">' + data[i].truncatedAddr + '</td>';
            continue;
        }

        html += '<td style="cursor: pointer">' + data[i].place + '</td>';
        html += '<td style="cursor: pointer">' + data[i].truncatedAddr + '</td>';
        html += '<td class="text-center">';
        html += '<input class="form-check-input" type="checkbox" id="' + ticketID + '-is-corrected" disabled ';
        if (data[i].isCorrected === 'TRUE') html += 'checked ';
        html += '/>';
        html += '</td>';
        html += '<td class="text-center">' + data[i].status + '</td>';
        html += '</tr>';
    }

    document.querySelector('#data-container .table tbody').innerHTML = html;
    document.getElementById('data-container').classList.remove('d-none');

    setCoordModal(data);
}

function setCoordModal(dataList) {
    dataModalElem.addEventListener('shown.bs.modal', async event => {
        const idx = event.relatedTarget.getAttribute('data-bs-idx');

        drawCoordModal (dataList, idx);
    });
}

async function drawCoordModal(dataList, idx) {
    const data = dataList[idx];

    // 스크롤 초기화
    document.querySelector('.modal-body').scrollTop = 0;

    // 로컬프로필 아이디 복사
    navigator.clipboard.writeText(data.localProfileID)
        .then(() => {})
        .catch();

    // 장소 정보 입력
    document.getElementById('data-modal-lp-id').innerHTML = data.localProfileID;
    document.getElementById('data-modal-place').innerHTML = data.place;
    document.getElementById('data-modal-category').innerHTML = '(' + data.category + ')';
    document.getElementById('data-modal-addr-load').innerHTML = data.addrLoad;
    document.getElementById('data-modal-addr-num').innerHTML = data.addrNum;

    // 상태값 설정
    document.getElementById('data-modal-is-corr').checked = (data.isCorrected === 'TRUE');
    document.getElementById('data-modal-status').value = data.isCorrected === 'FALSE' && data.status === '' ? '' : data.isCorrected === 'TRUE' && data.status === '' ? '처리완료' : data.status;

    // 장소 검색
    const keywordsMap = new Map();
    keywordsMap.set('place', encodeURIComponent(data.place));
    keywordsMap.set('addrLoad', encodeURIComponent(data.addrLoad !== 'NULL' ? data.addrLoad : ''));
    keywordsMap.set('addrNum', encodeURIComponent(data.addrNum !== 'NULL' ? data.addrNum : ''));
    const url = `/api/v1/coord-corr/get-result?place=${keywordsMap.get('place')}&addrLoad=${keywordsMap.get('addrLoad')}&addrNum=${keywordsMap.get('addrNum')}`;

    try {
        const response = await fetch(url);
        const result = await response.json();

        drawSearchPlaceResult(result, keywordsMap);
    } catch (e) {
        console.error(e);
    }

    // 네이버지도 불러오기
    const elemId = 'data-modal-map-main';
    const geocodeMap = new Map();
    geocodeMap.set('lat1', data.coordinatesX);
    geocodeMap.set('lng1', data.coordinatesY);
    geocodeMap.set('lat2', data.geocodeLat);
    geocodeMap.set('lng2', data.geocodeLon);
    drawMap(elemId, geocodeMap);

    setCoordModalEvents(dataList, idx);
}

function setCoordModalEvents(dataList, idx) {
    document.getElementById('data-modal-status').onchange = function() {
        if (this.value === '보정 완료' || this.value === '오차 없음') {
            document.getElementById('data-modal-is-corr').checked = true;
        } else {
            document.getElementById('data-modal-is-corr').checked = false;
        }
    };

    document.getElementById('data-modal-update').onclick = async function () {
        const isCorrected = document.getElementById('data-modal-is-corr').checked === true ? 'TRUE' : 'FALSE';
        const status = document.getElementById('data-modal-status').value === '보정 전' || document.getElementById('data-modal-status').value === '보정 완료' ? '' : document.getElementById('data-modal-status').value;
        const idxOnSheet = Number(idx) + 2;
        const url = `/api/v1/coord-corr/update?idx=${encodeURIComponent(idxOnSheet)}&isCorrected=${encodeURIComponent(isCorrected)}&status=${encodeURIComponent(status)}`;

        const response = await fetch(url);

        if (response.ok) {
            document.querySelector('[data-bs-idx="' + idx + '"] input[type="checkbox"]').checked = isCorrected === 'TRUE';
            document.querySelector('[data-bs-idx="' + idx + '"] td:last-child').innerHTML = status;

            moveToNextRow(dataList, idx);
        } else {
            const errorMsg = await response.text();
            console.error(errorMsg);
        }
    };
}function moveToNextRow(dataList, idx) {
    idx = Number(idx) + 1;

    // 더 이상 데이터가 없으면 모달 닫기
    if (idx >= dataList.length) {
        alert("마지막 데이터입니다.");
        dataModal.hide();
        return;
    }

    drawCoordModal (dataList, idx);
}

// 지도 그리기
function drawMap(elemId, geocodeMap) {
    const lat1 = parseFloat(geocodeMap.get('lat1'));
    const lng1 = parseFloat(geocodeMap.get('lng1'));
    const lat2 = parseFloat(geocodeMap.get('lat2'));
    const lng2 = parseFloat(geocodeMap.get('lng2'));

    // 좌표값이 유효한지 체크
    if (isNaN(lat1) || isNaN(lng1)) return;

    const mapOptions = {
        center: new naver.maps.LatLng(lat1, lng1),
        zoom: 15
    };

    // 전달받은 elemId 사용
    const map = new naver.maps.Map(elemId, mapOptions);

    const marker1 = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat1, lng1),
        map: map,
        icon: {
            content: '<div style="background-color:#ff8729; width:20px; height:20px; border-radius:50%; border:2px solid white;"></div>',
            anchor: new naver.maps.Point(5, 5)
        }
    });

    if (!isNaN(lat2) && !isNaN(lng2)) {
        const marker2 = new naver.maps.Marker({
            position: new naver.maps.LatLng(lat2, lng2),
            map: map,
            icon: {
                content: '<div style="background-color:#33d535; width:20px; height:20px; border-radius:50%; border:2px solid white;"></div>',
                anchor: new naver.maps.Point(5, 5)
            }
        });

        let bounds = new naver.maps.LatLngBounds();
        bounds.extend(marker1.getPosition());
        bounds.extend(marker2.getPosition());
        map.fitBounds(bounds);
    }
}

// 장소 결과 출력
async function drawSearchPlaceResult(data, keywordsMap) {
    let html = '';

    // data를 볼러오지 못했을 경우 함수 종료
    if (data.list.length === undefined) {
        html += '<div id="result-msg" class="alert alert-danger" role="alert">';
        html += '데이터를 불러오는 중에 오류가 발생했어요.';
        html += '</div>';

        document.getElementById('data-modal-search-result-container').innerHTML = html;
        document.getElementById('data-modal-search-result-container').classList.remove('d-none');

        return false;
    }

    // data.list가 없을 경우 함수 종료
    if (data.list.length === 0) {
        const searchResultLength = await getSearchResultLength(keywordsMap);

        html += '<div id="result-msg" class="alert alert-secondary" role="alert">';
        html += '없어요 (X)<small class="ms-3">(검색 결과: ' + searchResultLength + '개)</small>';
        html += '</div>';

        document.getElementById('data-modal-search-result-container').innerHTML = html;
        document.getElementById('data-modal-search-result-container').classList.remove('d-none');

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
        html += '</li>';
    }

    html += '</ul>';
    html += '</div>';

    document.getElementById('data-modal-search-result-container').innerHTML = html;
    document.getElementById('data-modal-search-result-container').classList.remove('d-none');
}

// '이름, 도로명주소, 지번주소'의 검색 결과가 있는지 여부 확인
async function getSearchResultLength(keywordsMap) {
    const url = `/api/v1/coord-corr/get-search-result-length?place=${keywordsMap.get('place')}&addrLoad=${keywordsMap.get('addrLoad')}&addrNum=${keywordsMap.get('addrNum')}&addrTruncated=${keywordsMap.get('addrTruncated')}`;

    try {
        const response = await fetch(url);
        return await response.text();
    } catch (e) {
        console.error(e);
    }
}

// 처리 결과 저장
async function update() {}

// 초기화
function initCoordData() {
    document.getElementById('data-container').classList.add('d-none');
    document.querySelector('#data-container .table tbody').innerHTML = '';
}