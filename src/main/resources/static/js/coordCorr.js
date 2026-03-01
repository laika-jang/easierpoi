setCoordEvents();

function setCoordEvents() {
    // 구글시트 데이터 불러오기
    document.getElementById('get-coord-data').addEventListener('click', coordCorr);

    // 입력폼 초기화
    document.getElementById('init-data').addEventListener('click', initCoordData);
}

// 구글시트 데이터 불러오기
async function coordCorr() {
    const url = `/api/v1/coord-corr/get-data`;

    try {
        const response = await fetch(url);
        const data = await response.json();

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

        html += '<tr>';
        html += '<th scope="row">' + ticketID + '</th>';
        html += '<td>' + data[i].place + '</td>';
        html += '<td>' + data[i].truncatedAddr + '</td>';
        html += '<td class="text-center">';
        html += '<input class="form-check-input" type="checkbox" id="' + ticketID + '-is-corrected" disabled ';
        if (data[i].isCorrected === 'TRUE') html += 'checked ';
        html += '/>';
        html += '</td>';
        html += '<td class="text-center">'

        html += '<select class="form-select form-select-sm" aria-label="data-status" id="' + ticketID + '-status">';
        html += '<option value=""';
        if (data[i].status === '' && data[i].isCorrected === 'FALSE') html += ' selected';
        html += '>선택하세요</option>';
        html += '<option value="처리완료"';
        if (data[i].status === '' && data[i].isCorrected === 'TRUE') html += ' selected';
        html += '>처리완료</option>';
        html += '<option value="폐업"';
        if (data[i].status === '폐업') html += ' selected';
        html += '>폐업</option>';
        html += '<option value="검색 결과 X"';
        if (data[i].status === '검색 결과 X') html += ' selected';
        html += '>검색 결과 X</option>';
        html += '<option value="오차 없음"';
        if (data[i].status === '오차 없음') html += ' selected';
        html += '>오차 없음</option>';
        html += '<option value="포털 수정 필요"';
        if (data[i].status === '포털 수정 필요') html += ' selected';
        html += '>포털 수정 필요</option>';
        html += '<option value="검색 결과 상이"';
        if (data[i].status === '검색 결과 상이') html += ' selected';
        html += '>검색 결과 상이</option>';
        html += '</select>';

        html += '</td>';
        html += '</tr>';
    }

    document.querySelector('#data-container .table tbody').innerHTML = html;
    document.getElementById('data-container').classList.remove('d-none');

    setCoordListEvents();
}

function setCoordListEvents() {
    //
}

// 초기화
function initCoordData() {
    document.getElementById('data-container').classList.add('d-none');
    document.querySelector('#data-container .table tbody').innerHTML = '';
}