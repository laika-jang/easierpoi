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

    // 초기화
    document.querySelector('.modal-body').scrollTop = 0;
    document.getElementById('data-modal-search-result-container').innerHTML = '';

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
    document.getElementById('add-search-place').value = data.place;
    document.getElementById('add-search-addr').value = data.truncatedAddr;

    // 상태값 설정
    document.getElementById('data-modal-is-corr').checked = (data.isCorrected === 'TRUE');
    document.getElementById('data-modal-status').value = data.isCorrected === 'FALSE' && data.status === '' ? '' : data.isCorrected === 'TRUE' && data.status === '' ? '처리완료' : data.status;

    // 장소 검색
    const keywordsMap = new Map();
    keywordsMap.set('place', encodeURIComponent(data.place));
    keywordsMap.set('addrLoad', encodeURIComponent(data.addrLoad !== 'NULL' ? data.addrLoad : ''));
    keywordsMap.set('addrNum', encodeURIComponent(data.addrNum !== 'NULL' ? data.addrNum : ''));
    const url = `/api/v1/coord-corr/get-result?place=${keywordsMap.get('place')}&addrLoad=${keywordsMap.get('addrLoad')}&addrNum=${keywordsMap.get('addrNum')}`;
    let geocodeMap = new Map();

    try {
        const response = await fetch(url);
        const result = await response.json();

        geocodeMap = await drawSearchPlaceResult(result, keywordsMap);
    } catch (e) {
        console.error(e);
    }

    // 네이버지도 불러오기
    const elemId = 'data-modal-map-main';
    geocodeMap.set('lat1', data.coordinatesX);
    geocodeMap.set('lng1', data.coordinatesY);
    geocodeMap.set('label1', '당근');
    geocodeMap.set('lat2', data.geocodeLat);
    geocodeMap.set('lng2', data.geocodeLon);
    geocodeMap.set('label2', '네이버');
    drawMap(elemId, geocodeMap);

    setCoordModalEvents(dataList, idx);
}

// 장소 검색 결과 출력
async function drawSearchPlaceResult(data, keywordsMap) {
    let html = '';
    let coordMap = new Map();

    // data를 볼러오지 못했을 경우 함수 종료
    if (data.list.length === undefined) {
        html += '<div id="result-msg" class="alert alert-danger" role="alert">';
        html += '데이터를 불러오는 중에 오류가 발생했어요.';
        html += '</div>';

        document.getElementById('data-modal-search-result-container').innerHTML = html;
        document.getElementById('data-modal-search-result-container').classList.remove('d-none');

        return coordMap;
    }

    // data.list가 없을 경우 함수 종료
    if (data.list.length === 0) {
        const searchResultLength = await getSearchResultLength(keywordsMap);

        html += '<div id="result-msg" class="alert alert-secondary" role="alert">';
        html += '없어요 (X)<small class="ms-3">(검색 결과: ' + searchResultLength + '개)</small>';
        html += '</div>';

        document.getElementById('data-modal-is-corr').checked = false;
        document.getElementById('data-modal-status').value = Number(searchResultLength) > 0 ? '폐업' : '검색 결과 X';
        document.getElementById('data-modal-search-result-container').innerHTML = html;
        document.getElementById('data-modal-search-result-container').classList.remove('d-none');

        return coordMap;
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

        coordMap.set('lat' + (i + 3), (parseFloat(data.list[i].mapy)/10000000).toString());
        coordMap.set('lng' + (i + 3), (parseFloat(data.list[i].mapx)/10000000).toString());
        coordMap.set('label' + (i + 3), data.list[i].place);
    }

    html += '</ul>';
    html += '</div>';

    document.getElementById('data-modal-search-result-container').innerHTML = html;
    document.getElementById('data-modal-search-result-container').classList.remove('d-none');

    return coordMap;
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

// 지도 그리기
function drawMap(elemId, geocodeMap) {
    const pointList = [];

    for (let i = 1; i <= 7; i++) {
        const latKey = `lat${i}`;
        const lngKey = `lng${i}`;
        const labelKey = `label${i}`;
        if (geocodeMap.has(latKey) && geocodeMap.has(lngKey) && geocodeMap.has(labelKey)) {
            pointList.push({
                lat: parseFloat(geocodeMap.get(latKey)),
                lng: parseFloat(geocodeMap.get(lngKey)),
                label: geocodeMap.get(labelKey),
                index: i
            });
        }
    }

    if (pointList.length === 0) return;

    const mapOptions = {
        center: new naver.maps.LatLng(pointList[0].lat, pointList[0].lng),
        zoom: 15
    };
    const map = new naver.maps.Map(elemId, mapOptions);
    const bounds = new naver.maps.LatLngBounds();

    // 2. 마커 및 말풍선 생성 루프
    pointList.forEach((point) => {
        const color = point.index === 1 ? '#ff9800' : (point.index === 2 ? '#1dc800' : '#3F51B5');
        const size = point.index === 1 || point.index === 2 ? '32px' : '18px';
        const iconType = point.index === 1 || point.index === 2 ? 'bi-geo-alt-fill' : 'bi-' + (point.index - 2) + '-circle-fill';
        const anchorPointX = point.index === 1 || point.index === 2 ? 18 : 9;
        const anchorPointY = point.index === 1 || point.index === 2 ? 32 : 9;

        const marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(point.lat, point.lng),
            map: map,
            icon: {
                content: `<i class="bi ${iconType}" style="font-size: ${size}; color: ${color};"></i>`,
                anchor: new naver.maps.Point(`${anchorPointX}`, `${anchorPointY}`)
            }
        });

        // 3. 말풍선(InfoWindow) 설정
        const infoWindow = new naver.maps.InfoWindow({
            content: '<div class="p-2"><small>' + point.label + '</small></div>',
            backgroundColor: "#fff",
            borderColor: "#ccc",
            borderWidth: 1,
            disableAnchor: false,
            anchorSize: new naver.maps.Size(10, 10),
            pixelOffset: new naver.maps.Point(0, -10)
        });

        // 4. 마커 클릭 시 말풍선 열기/닫기 이벤트
        naver.maps.Event.addListener(marker, 'click', function() {
            if (infoWindow.getMap()) {
                infoWindow.close();
            } else {
                infoWindow.open(map, marker);
            }
        });

        bounds.extend(marker.getPosition());
    });

    if (pointList.length > 1) {
        map.fitBounds(bounds);
    }
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

    document.getElementById('add-search-button').onclick = async function () {
        await additionalSearch(dataList, idx);
    };
}

function moveToNextRow(dataList, idx) {
    idx = Number(idx) + 1;

    // 더 이상 데이터가 없으면 모달 닫기
    if (idx >= dataList.length) {
        alert("마지막 데이터입니다.");
        dataModal.hide();
        return;
    }

    drawCoordModal (dataList, idx);
}

async function additionalSearch(dataList, idx) {
    const data = dataList[idx];
    const place = document.getElementById('add-search-place').value;
    const addrRaw = document.getElementById('add-search-addr').value;
    let addrLoad = '';
    let addrNum = '';

    // 도로명 주소 및 지번 주소 추출
    try {
        const response = await fetch(`/api/v1/validity/get-addr?addr=${encodeURIComponent(addrRaw)}`);
        const addrMap = await response.json();

        if (addrMap.addrLoad !== undefined) addrLoad = addrMap.addrLoad;
        if (addrMap.addrNum !== undefined) addrNum = addrMap.addrNum;
    } catch (e) {
        console.error(e);
    }

    // 검색
    const keywordsMap = new Map();
    keywordsMap.set('place', encodeURIComponent(place));
    keywordsMap.set('addrLoad', encodeURIComponent(addrLoad));
    keywordsMap.set('addrNum', encodeURIComponent(addrNum));
    const url = `/api/v1/coord-corr/get-result?place=${keywordsMap.get('place')}&addrLoad=${keywordsMap.get('addrLoad')}&addrNum=${keywordsMap.get('addrNum')}`;
    let geocodeMap = new Map();

    try {
        const response = await fetch(url);
        const result = await response.json();

        geocodeMap = await drawSearchPlaceResult(result, keywordsMap);
    } catch (e) {
        console.error(e);
    }

    // 네이버지도 불러오기
    const elemId = 'data-modal-map-main';
    geocodeMap.set('lat1', data.coordinatesX);
    geocodeMap.set('lng1', data.coordinatesY);
    geocodeMap.set('label1', '당근');
    geocodeMap.set('lat2', data.geocodeLat);
    geocodeMap.set('lng2', data.geocodeLon);
    geocodeMap.set('label2', '네이버');
    drawMap(elemId, geocodeMap);

    setCoordModalEvents(dataList, idx);
}

// 초기화
function initCoordData() {
    document.getElementById('data-container').classList.add('d-none');
    document.querySelector('#data-container .table tbody').innerHTML = '';
}