const API_BASE = "";

// === ТОКЕНЫ ===
const getAccess  = () => sessionStorage.getItem('accessToken');
const getRefresh = () => sessionStorage.getItem('refreshToken');

function setTokens(jwt) {
    if (!jwt) return;
    if (jwt.accessToken)  sessionStorage.setItem('accessToken',  jwt.accessToken);
    if (jwt.refreshToken) sessionStorage.setItem('refreshToken', jwt.refreshToken);
    updateAuthUI(true);
}

function clearTokens() {
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
    userUuid = null;
    updateAuthUI(false);
}

// === СОСТОЯНИЕ ===
let userUuid = null;
let currentGameUuid = null;
let isGameOver = false;
let mySymbol = null;
let pendingMove = null;

let currentCells = [
    ["EMPTY", "EMPTY", "EMPTY"],
    ["EMPTY", "EMPTY", "EMPTY"],
    ["EMPTY", "EMPTY", "EMPTY"]
];

window.onload = () => {
    const signupBtn         = document.getElementById('signup-btn');
    const loginBtn          = document.getElementById('login-btn');
    const logoutBtn         = document.getElementById('logout-btn');
    const actionBtn         = document.getElementById('action-btn');
    const loadGamesBtn      = document.getElementById('load-games-btn');
    const loadHistoryBtn    = document.getElementById('load-history-btn');
    const loadLeaderboardBtn= document.getElementById('load-leaderboard-btn');
    const myIdBtn           = document.getElementById('my-id-btn');

    if (signupBtn)          signupBtn.onclick          = handleSignUp;
    if (loginBtn)           loginBtn.onclick           = handleLogin;
    if (logoutBtn)          logoutBtn.onclick          = handleLogout;
    if (actionBtn)          actionBtn.onclick          = handleActionClick;
    if (loadGamesBtn)       loadGamesBtn.onclick       = loadGames;
    if (loadHistoryBtn)     loadHistoryBtn.onclick     = loadHistory;
    if (loadLeaderboardBtn) loadLeaderboardBtn.onclick = loadLeaderboard;
    if (myIdBtn)            myIdBtn.onclick            = showMyProfile;

    updateAuthUI(!!getAccess());
    renderBoard(currentCells);

    if (getAccess()) loadMyProfile().then(renderUserUuid);
};

// === UI АВТОРИЗАЦИИ ===
function updateAuthUI(authenticated) {
    const statusEl = document.getElementById('auth-status');
    if (statusEl) {
        statusEl.innerText = authenticated ? 'авторизован' : 'не авторизован';
        statusEl.classList.toggle('online', authenticated);
    }
    const uuidEl = document.getElementById('my-uuid-display');
    if (uuidEl && !authenticated) uuidEl.innerText = '—';
}

function renderUserUuid(profile) {
    const el = document.getElementById('my-uuid-display');
    if (!el) return;
    el.innerText = (profile && profile.uuid) || userUuid || '—';
}

// === ОБЁРТКА XHR С АВТО-REFRESH ===
function basicFetch(url, options = {}, retry = true) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open(options.method || 'GET', url, true);

        const headers = Object.assign({}, options.headers || {});
        const token = getAccess();
        if (token && !headers['Authorization']) {
            headers['Authorization'] = 'Bearer ' + token;
        }
        for (const [k, v] of Object.entries(headers)) {
            xhr.setRequestHeader(k, v);
        }

        xhr.onload = () => resolve({
            ok: xhr.status >= 200 && xhr.status < 300,
            status: xhr.status,
            headers: { get: (h) => xhr.getResponseHeader(h) },
            json: () => Promise.resolve(JSON.parse(xhr.responseText)),
            text: () => Promise.resolve(xhr.responseText)
        });
        xhr.onerror = () => reject(new Error('Network error'));
        xhr.send(options.body || null);
    }).then(async (response) => {
        if (response.status === 401 && retry && getRefresh()) {
            const ok = await tryRefreshAccess();
            if (ok) return basicFetch(url, options, false);
            clearTokens();
        }
        return response;
    });
}

async function tryRefreshAccess() {
    const refresh = getRefresh();
    if (!refresh) return false;
    try {
        const response = await basicFetch(`${API_BASE}/auth/refresh/access`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken: refresh })
        }, false);
        if (!response.ok) return false;
        const data = await response.json();
        setTokens(data);
        return true;
    } catch (e) {
        return false;
    }
}

// === РЕГИСТРАЦИЯ ===
async function handleSignUp() {
    const login = document.getElementById('login-input').value;
    const password = document.getElementById('password-input').value;
    const payload = { login, password };
    logRequest(payload);

    try {
        const response = await basicFetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        await handleResponseStatus(response);
        alert('Регистрация успешна! Теперь нажмите «Войти».');
    } catch (error) {
        logError(error);
    }
}

// === ЛОГИН ===
async function handleLogin() {
    const login = document.getElementById('login-input').value;
    const password = document.getElementById('password-input').value;
    if (!login || !password) {
        alert('Введите логин и пароль');
        return;
    }
    const payload = { login, password };
    logRequest(payload);

    try {
        const response = await basicFetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await handleResponseStatus(response);
        if (data && data.accessToken) {
            setTokens(data);
            await loadMyProfile().then(renderUserUuid);
            alert('Успешный вход!');
        } else {
            alert('Сервер не вернул accessToken.');
        }
    } catch (error) {
        logError(error);
    }
}

// === ВЫХОД ===
function handleLogout() {
    clearTokens();
    resetGameState();
    renderBoard(currentCells);
    alert('Вы вышли из аккаунта.');
}

// === ПРОФИЛЬ ===
async function loadMyProfile() {
    if (!getAccess()) return null;
    try {
        const response = await basicFetch(`${API_BASE}/auth/me`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await handleResponseStatus(response);
        if (data && data.uuid) userUuid = data.uuid;
        return data;
    } catch (e) {
        logError(e);
        return null;
    }
}

async function showMyProfile() {
    const displayEl = document.getElementById('my-id-display');
    displayEl.classList.add('visible');

    if (!getAccess()) {
        displayEl.innerHTML = 'Сначала нажмите «Войти».';
        return;
    }

    displayEl.innerText = 'Загрузка...';
    const data = await loadMyProfile();
    if (!data) {
        displayEl.innerText = 'Ошибка загрузки профиля';
        return;
    }

    const roles = (data.roles || []).join(', ');
    displayEl.innerHTML = `
        <strong>Логин:</strong> ${data.login || '(без имени)'}<br>
        <strong>UUID:</strong> ${data.uuid}<br>
        <strong>Роли:</strong> ${roles}<br>
        <details>
            <summary>Полный ответ</summary>
            <pre>${JSON.stringify(data, null, 2)}</pre>
        </details>
    `;
    renderUserUuid(data);
}

// === ИГРОВАЯ ЛОГИКА ===
function clearGamePolling() {
    if (window.gamePollTimeout) {
        clearTimeout(window.gamePollTimeout);
        window.gamePollTimeout = null;
    }
}

function resetGameState() {
    clearGamePolling();
    currentGameUuid = null;
    isGameOver = false;
    mySymbol = null;
    pendingMove = null;
    currentCells = [
        ["EMPTY", "EMPTY", "EMPTY"],
        ["EMPTY", "EMPTY", "EMPTY"],
        ["EMPTY", "EMPTY", "EMPTY"]
    ];
    const uuidEl = document.getElementById('game-uuid');
    if (uuidEl) uuidEl.innerText = 'Не создана';
    const statusEl = document.getElementById('game-status');
    if (statusEl) {
        statusEl.innerText = 'Ожидание старта';
        statusEl.className = 'status-badge bg-not-started';
    }
    const boardEl = document.getElementById('game-board');
    if (boardEl) boardEl.classList.add('board-disabled');
    const btn = document.getElementById('action-btn');
    if (btn) {
        btn.innerText = 'Новая партия';
        btn.className = 'action-btn btn-start';
        btn.disabled = false;
    }
}

function handleActionClick() {
    if (!getAccess()) { alert('Сначала войдите'); return; }
    if (!currentGameUuid || isGameOver) createNewGame();
    else sendGameState();
}

async function createNewGame() {
    resetGameState();
    renderBoard(currentCells);
    document.getElementById('json-error').innerText = "Ошибок нет";

    const opponent = document.getElementById('opponent-type').value;
    const payload = { opponent };
    logRequest(payload);

    try {
        const response = await basicFetch(`${API_BASE}/game`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await handleResponseStatus(response);
        if (data) applyGameState(data);
    } catch (error) {
        logError(error);
    }
}

async function sendGameState() {
    if (!pendingMove) {
        document.getElementById('json-error').innerText = "Ошибка: сначала сделайте ход!";
        return;
    }
    document.getElementById('json-error').innerText = "Ошибок нет";
    const payload = { cells: currentCells };
    logRequest(payload);

    try {
        const response = await basicFetch(`${API_BASE}/game/${currentGameUuid}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await handleResponseStatus(response);
        if (data) {
            pendingMove = null;
            applyGameState(data);
        }
    } catch (error) {
        logError(error);
    }
}

async function pollGameStatus() {
    if (!currentGameUuid || isGameOver) return;
    try {
        const response = await basicFetch(`${API_BASE}/game/${currentGameUuid}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (response.ok) {
            const data = await response.json();
            applyGameState(data);
        }
    } catch (e) {
        console.error("Ошибка автообновления:", e);
    }
}

function applyGameState(data) {
    if (!data) return;
    currentGameUuid = data.uuid || currentGameUuid;
    if (data.cells) currentCells = data.cells;
    if (data.xxPlayerUuid && !userUuid) userUuid = data.xxPlayerUuid;
    if (data.ooPlayerUuid && !userUuid) userUuid = data.ooPlayerUuid;

    determinePlayerSymbol(data);
    document.getElementById('game-uuid').innerText = currentGameUuid;
    updateGameStatusUI(data);
    renderBoard(currentCells);
}

async function loadGames() {
    if (!getAccess()) { alert('Сначала войдите'); return; }
    const listEl = document.getElementById('games-list');
    listEl.innerHTML = '<em>Загрузка...</em>';

    try {
        const response = await basicFetch(`${API_BASE}/game?page=0&size=10`, { method: 'GET' });
        const data = await handleResponseStatus(response);
        renderGamesList(data);
    } catch (error) {
        listEl.innerHTML = '<em style="color:#dc3545;">Ошибка загрузки списка</em>';
        logError(error);
    }
}

function renderGamesList(data) {
    const listEl = document.getElementById('games-list');
    listEl.innerHTML = '';
    const games = (data && (data.games || data.content || data.items)) || [];

    if (games.length === 0) {
        listEl.innerHTML = '<em>Нет доступных игр</em>';
        return;
    }

    games.forEach(game => {
        const item = document.createElement('div');
        item.className = 'game-item';

        const info = document.createElement('div');
        info.className = 'game-info';
        info.innerHTML = `
            <strong>${game.uuid}</strong>
            <small>Статус: ${game.status}, Противник: ${game.opponentType}</small>
        `;

        const isMyGame = userUuid && game.xxPlayerUuid === userUuid;
        const joinBtn = document.createElement('button');
        joinBtn.className = 'action-btn btn-move';
        joinBtn.innerText = isMyGame ? 'Ваша игра' : 'Присоединиться';
        joinBtn.disabled = isMyGame;
        joinBtn.onclick = () => joinGame(game.uuid);

        item.appendChild(info);
        item.appendChild(joinBtn);
        listEl.appendChild(item);
    });
}

async function joinGame(gameUuid) {
    logRequest({});
    try {
        const response = await basicFetch(`${API_BASE}/game/${gameUuid}/join`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({})
        });
        const data = await handleResponseStatus(response);
        if (data) applyGameState(data);
        alert('Вы присоединились к игре!');
    } catch (error) {
        logError(error);
    }
}

function determinePlayerSymbol(gameDto) {
    if (!gameDto || !userUuid) { mySymbol = null; return; }
    if (gameDto.xxPlayerUuid === userUuid) mySymbol = "X";
    else if (gameDto.ooPlayerUuid === userUuid) mySymbol = "O";
    else mySymbol = null;
}

function toggleCellStatus(row, col) {
    const current = currentCells[row][col];
    if (pendingMove && pendingMove.row === row && pendingMove.col === col) {
        currentCells[row][col] = "EMPTY";
        pendingMove = null;
        renderBoard(currentCells);
        return;
    }
    if (pendingMove) {
        currentCells[pendingMove.row][pendingMove.col] = "EMPTY";
        pendingMove = null;
    }
    if (current === "EMPTY" && mySymbol) {
        currentCells[row][col] = mySymbol;
        pendingMove = { row, col };
        renderBoard(currentCells);
    }
}

function renderBoard(cells) {
    const boardEl = document.getElementById('game-board');
    boardEl.innerHTML = '';
    for (let r = 0; r < 3; r++) {
        const tr = document.createElement('tr');
        for (let c = 0; c < 3; c++) {
            const status = cells[r][c];
            const td = document.createElement('td');
            td.className = `status-${status}`;
            td.textContent = status === "X" ? 'X' : status === "O" ? 'O' : '·';
            if (!boardEl.classList.contains('board-disabled')) {
                td.onclick = () => toggleCellStatus(r, c);
            }
            tr.appendChild(td);
        }
        boardEl.appendChild(tr);
    }
    updateRequestPreview();
}

function updateGameStatusUI(gameDto) {
    const statusEl = document.getElementById('game-status');
    const btn = document.getElementById('action-btn');
    const boardEl = document.getElementById('game-board');
    clearGamePolling();

    const status = gameDto.status;
    const makesNextMove = gameDto.makesNextMove;
    const isFinished = status === "X_WINS" || status === "O_WINS" || status === "DRAW";
    const isWaiting = status === "WAITING_FOR_OPPONENT";
    const isMyTurn = !isFinished && !isWaiting
        && userUuid && makesNextMove && makesNextMove === userUuid;

    if (isFinished) {
        isGameOver = true;
        boardEl.classList.add('board-disabled');
        btn.innerText = "Новая партия";
        btn.className = "action-btn btn-start";
        btn.disabled = false;

        let msg = "Игра завершена";
        if (status === "X_WINS") { msg = "Победа Крестиков! 🎉"; statusEl.className = "status-badge bg-game-over"; }
        if (status === "O_WINS") { msg = "Победа Ноликов! 🤖"; statusEl.className = "status-badge bg-game-over"; }
        if (status === "DRAW")   { msg = "Ничья! 🤝";            statusEl.className = "status-badge bg-not-started"; }
        statusEl.innerText = msg;
        setTimeout(() => alert(msg), 100);
        return;
    }

    isGameOver = false;
    btn.innerText = "Отправить ход";
    btn.className = "action-btn btn-move";
    statusEl.className = "status-badge bg-in-game";

    if (isWaiting) statusEl.innerText = "Ожидание оппонента";
    else if (status === "X_MOVES_NEXT") statusEl.innerText = "Ход Крестиков";
    else if (status === "O_MOVES_NEXT") statusEl.innerText = "Ход Ноликов";

    if (isMyTurn) {
        boardEl.classList.remove('board-disabled');
        btn.disabled = false;
    } else {
        boardEl.classList.add('board-disabled');
        btn.disabled = true;
        window.gamePollTimeout = setTimeout(pollGameStatus, 1000);
    }
}

async function loadHistory() {
    const listEl = document.getElementById('history-list');
    if (!getAccess()) {
        listEl.innerHTML = '<em style="color:#dc3545;">Сначала войдите</em>';
        return;
    }
    listEl.innerHTML = '<em>Загрузка...</em>';

    try {
        const response = await basicFetch(`${API_BASE}/stats/game/history`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await handleResponseStatus(response);
        renderHistory(data);
    } catch (error) {
        listEl.innerHTML = '<em style="color:#dc3545;">Ошибка загрузки истории</em>';
        logError(error);
    }
}

function renderHistory(data) {
    const listEl = document.getElementById('history-list');
    listEl.innerHTML = '';

    const histories = (data && (data.histories || data.items || [])) || [];

    if (histories.length === 0) {
        listEl.innerHTML = '<em>Завершённых игр пока нет</em>';
        return;
    }

    histories.forEach(g => {
        const item = document.createElement('div');
        item.className = 'history-item';

        const info = document.createElement('div');
        info.className = 'history-info';
        const date = g.createdAt ? new Date(g.createdAt).toLocaleString() : '—';
        info.innerHTML = `
            <strong>${g.uuid}</strong>
            <small>Противник: ${g.opponentType}, дата: ${date}</small>
        `;

        const badge = document.createElement('div');
        badge.className = 'history-status ' + statusClass(g, userUuid);
        badge.textContent = statusLabel(g, userUuid);

        item.appendChild(info);
        item.appendChild(badge);
        listEl.appendChild(item);
    });
}

function statusClass(g, myUuid) {
    if (g.status === 'DRAW') return 'draw';
    if (g.status === 'X_WINS') return g.xxPlayerUuid === myUuid ? 'win' : 'lose';
    if (g.status === 'O_WINS') return g.ooPlayerUuid === myUuid ? 'win' : 'lose';
    return 'draw';
}

function statusLabel(g, myUuid) {
    if (g.status === 'DRAW') return 'Ничья';
    const isX = g.xxPlayerUuid === myUuid;
    if (g.status === 'X_WINS') return isX ? 'Победа' : 'Поражение';
    if (g.status === 'O_WINS') return isX ? 'Поражение' : 'Победа';
    return g.status;
}

// === ТАБЛИЦА ЛИДЕРОВ ===
async function loadLeaderboard() {
    const listEl = document.getElementById('leaderboard-list');
    if (!getAccess()) {
        listEl.innerHTML = '<em style="color:#dc3545;">Сначала войдите</em>';
        return;
    }

    const n = parseInt(document.getElementById('leaderboard-n').value, 10) || 5;
    listEl.innerHTML = '<em>Загрузка...</em>';

    try {
        const response = await basicFetch(`${API_BASE}/stats/game/leaderboard?n=${n}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await handleResponseStatus(response);
        renderLeaderboard(data);
    } catch (error) {
        listEl.innerHTML = '<em style="color:#dc3545;">Ошибка загрузки</em>';
        logError(error);
    }
}

function renderLeaderboard(data) {
    const listEl = document.getElementById('leaderboard-list');
    listEl.innerHTML = '';

    const players = (data && (data.players || data.entries || [])) || [];

    if (players.length === 0) {
        listEl.innerHTML = '<em>Пока нет данных</em>';
        return;
    }

    players.forEach((p, idx) => {
        const item = document.createElement('div');
        item.className = 'leaderboard-item';

        const rank = document.createElement('div');
        rank.className = 'rank';
        rank.textContent = `${idx + 1}.`;

        const info = document.createElement('div');
        info.className = 'player-info';
        info.innerHTML = `
            <strong>${p.login || '(без логина)'}</strong>
            <small>${p.uuid}</small>
        `;

        const ratio = document.createElement('div');
        ratio.className = 'ratio';
        const r = p.winRatio != null ? Number(p.winRatio).toFixed(2) : '0.00';
        ratio.textContent = `W/L: ${r}`;

        item.appendChild(rank);
        item.appendChild(info);
        item.appendChild(ratio);
        listEl.appendChild(item);
    });
}

// === ЛОГГЕРЫ ===
function logRequest(payload) {
    document.getElementById('json-request').innerText = JSON.stringify(payload, null, 2);
}
function logResponse(data) {
    document.getElementById('json-response').innerText = JSON.stringify(data, null, 2);
}
function logError(error) {
    document.getElementById('json-error').innerText = JSON.stringify({
        error: error.name, message: error.message
    }, null, 2);
}
function updateRequestPreview() {
    if (!currentGameUuid || isGameOver) {
        const opp = document.getElementById('opponent-type').value;
        logRequest({ opponent: opp });
    } else if (pendingMove) {
        logRequest({ cells: currentCells });
    }
}
async function handleResponseStatus(response) {
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({
            status: response.status,
            message: "Неизвестная ошибка сервера"
        }));
        document.getElementById('json-response').innerText = "{}";
        throw new Error(`[Status ${errorData.status || response.status}]: ${errorData.message}`);
    }
    if (response.status === 204 || response.headers.get('content-length') === '0') {
        const ok = { status: response.status, message: "Успешно (Ответ без тела)" };
        logResponse(ok);
        return ok;
    }
    const data = await response.json();
    logResponse(data);
    return data;
}