/**
 * HireNext Enterprise Placement Portal - Client Logic
 * Features: Pure OOP Java Backend, Auth, CV Upload & Resume Match Engine
 */

const API_DRIVES = '/api/drives';
const API_APPS = '/api/applications';
const API_STUDENTS = '/api/students';
const API_LOGIN = '/api/auth/login';
const API_REGISTER = '/api/auth/register';

// State
let drives = [];
let applications = [];
let registeredStudents = [];
let activeRole = 'STUDENT';
let currentUser = null;
let currentCvFilename = '';

// DOM Elements - Navigation & Auth
const btnStudentRole = document.getElementById('btnStudentRole');
const btnCompanyRole = document.getElementById('btnCompanyRole');
const studentView = document.getElementById('studentView');
const companyView = document.getElementById('companyView');

const authBar = document.getElementById('authBar');
const userBadge = document.getElementById('userBadge');
const userDisplayName = document.getElementById('userDisplayName');
const logoutBtn = document.getElementById('logoutBtn');

const openLoginBtn = document.getElementById('openLoginBtn');
const closeLoginBtn = document.getElementById('closeLoginBtn');
const loginModal = document.getElementById('loginModal');
const loginForm = document.getElementById('loginForm');

const openRegisterBtn = document.getElementById('openRegisterBtn');
const closeRegisterBtn = document.getElementById('closeRegisterBtn');
const registerModal = document.getElementById('registerModal');
const registerForm = document.getElementById('registerForm');

// KPI Counters
const kpiTotalDrives = document.getElementById('kpiTotalDrives');
const kpiMaxCtc = document.getElementById('kpiMaxCtc');
const kpiApplications = document.getElementById('kpiApplications');
const kpiAvgCtc = document.getElementById('kpiAvgCtc');

// Student Sub Tabs & CV Upload
const subTabApply = document.getElementById('subTabApply');
const subTabProfile = document.getElementById('subTabProfile');
const subSectionApply = document.getElementById('subSectionApply');
const subSectionProfile = document.getElementById('subSectionProfile');
const studentProfileForm = document.getElementById('studentProfileForm');

const dropzone = document.getElementById('dropzone');
const profCvFileInput = document.getElementById('profCvFileInput');
const fileNameDisplay = document.getElementById('fileNameDisplay');
const profCvText = document.getElementById('profCvText');

// Demo Account Chips
const demoStudent1Btn = document.getElementById('demoStudent1Btn');
const demoStudent2Btn = document.getElementById('demoStudent2Btn');
const demoCompanyBtn = document.getElementById('demoCompanyBtn');

// DOM Elements - Student Side
const studentDriveList = document.getElementById('studentDriveList');
const studentSearchInput = document.getElementById('studentSearchInput');
const studentRollLookup = document.getElementById('studentRollLookup');
const lookupBtn = document.getElementById('lookupBtn');
const myApplicationsList = document.getElementById('myApplicationsList');

// Modal Elements
const applyModal = document.getElementById('applyModal');
const modalDriveTitle = document.getElementById('modalDriveTitle');
const modalDriveId = document.getElementById('modalDriveId');
const closeModalBtn = document.getElementById('closeModalBtn');
const studentApplyForm = document.getElementById('studentApplyForm');

// DOM Elements - Company Side
const companyDriveForm = document.getElementById('companyDriveForm');
const companyDriveList = document.getElementById('companyDriveList');
const companyAppList = document.getElementById('companyAppList');
const studentDirectoryList = document.getElementById('studentDirectoryList');
const tabManageDrives = document.getElementById('tabManageDrives');
const tabReviewApps = document.getElementById('tabReviewApps');
const tabStudentDirectory = document.getElementById('tabStudentDirectory');
const panelDrives = document.getElementById('panelDrives');
const panelApplications = document.getElementById('panelApplications');
const panelStudents = document.getElementById('panelStudents');

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initCvDropzone();
    initAuthModals();
    initDemoShortcuts();
    fetchAllData();
});

function initNavigation() {
    btnStudentRole.addEventListener('click', () => switchRole('STUDENT'));
    btnCompanyRole.addEventListener('click', () => switchRole('COMPANY'));

    subTabApply.addEventListener('click', () => {
        subTabApply.classList.add('active');
        subTabProfile.classList.remove('active');
        subSectionApply.classList.remove('hidden');
        subSectionProfile.classList.add('hidden');
    });

    subTabProfile.addEventListener('click', () => {
        subTabProfile.classList.add('active');
        subTabApply.classList.remove('active');
        subSectionProfile.classList.remove('hidden');
        subSectionApply.classList.add('hidden');
    });

    tabManageDrives.addEventListener('click', () => setAdminTab(tabManageDrives, panelDrives));
    tabReviewApps.addEventListener('click', () => setAdminTab(tabReviewApps, panelApplications));
    tabStudentDirectory.addEventListener('click', () => {
        setAdminTab(tabStudentDirectory, panelStudents);
        renderStudentDirectory();
    });

    function setAdminTab(activeTab, activePanel) {
        [tabManageDrives, tabReviewApps, tabStudentDirectory].forEach(t => t.classList.remove('active'));
        [panelDrives, panelApplications, panelStudents].forEach(p => p.classList.add('hidden'));
        activeTab.classList.add('active');
        activePanel.classList.remove('hidden');
    }

    studentProfileForm.addEventListener('submit', handleProfileSave);
    lookupBtn.addEventListener('click', lookupMyApplications);
    studentSearchInput.addEventListener('input', renderStudentDrives);

    const packageFilter = document.getElementById('packageFilter');
    if (packageFilter) packageFilter.addEventListener('change', renderStudentDrives);

    const appStatusFilter = document.getElementById('appStatusFilter');
    if (appStatusFilter) appStatusFilter.addEventListener('change', renderCompanyApplications);

    const exportCsvBtn = document.getElementById('exportCsvBtn');
    if (exportCsvBtn) exportCsvBtn.addEventListener('click', exportApplicationsToCsv);

    closeModalBtn.addEventListener('click', () => applyModal.classList.add('hidden'));
    const closeCvModalBtn = document.getElementById('closeCvModalBtn');
    if (closeCvModalBtn) {
        closeCvModalBtn.addEventListener('click', () => document.getElementById('cvPreviewModal').classList.add('hidden'));
    }
    studentApplyForm.addEventListener('submit', handleStudentApply);
    companyDriveForm.addEventListener('submit', handleCompanyDrivePost);
}

function initCvDropzone() {
    dropzone.addEventListener('click', () => profCvFileInput.click());

    profCvFileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            currentCvFilename = file.name;
            fileNameDisplay.textContent = `📄 Selected: ${file.name}`;

            const reader = new FileReader();
            reader.onload = (event) => {
                const content = event.target.result;
                profCvText.value = content;
                showToast(`📄 CV "${file.name}" uploaded & text extracted!`);
            };
            reader.readAsText(file);
        }
    });
}

function initAuthModals() {
    openLoginBtn.addEventListener('click', () => loginModal.classList.remove('hidden'));
    closeLoginBtn.addEventListener('click', () => loginModal.classList.add('hidden'));

    openRegisterBtn.addEventListener('click', () => registerModal.classList.remove('hidden'));
    closeRegisterBtn.addEventListener('click', () => registerModal.classList.add('hidden'));

    loginForm.addEventListener('submit', handleLogin);
    registerForm.addEventListener('submit', handleRegister);
    logoutBtn.addEventListener('click', handleLogout);
}

function switchRole(role) {
    activeRole = role;
    if (role === 'STUDENT') {
        btnStudentRole.classList.add('active');
        btnCompanyRole.classList.remove('active');
        studentView.classList.remove('hidden');
        companyView.classList.add('hidden');
    } else {
        btnCompanyRole.classList.add('active');
        btnStudentRole.classList.remove('active');
        companyView.classList.remove('hidden');
        studentView.classList.add('hidden');
    }
}

// ============================================================
// CV / RESUME SMART MATCH ALGORITHM
// ============================================================
function computeSmartMatchScore(student, drive) {
    if (!student) return { score: 75, level: 'medium', label: '75% General Match', matchedSkills: [] };

    let score = 50;

    // 1. CGPA Component (30 Points max)
    if (student.cgpa >= drive.minCgpa) {
        score += 20;
        score += Math.min(10, (student.cgpa - drive.minCgpa) * 10);
    } else {
        score -= Math.min(30, (drive.minCgpa - student.cgpa) * 20);
    }

    // 2. Parsed CV Content & Skills Keyword Matching (50 Points max)
    const cvText = ((student.cvText || '') + ' ' + (student.skills || '')).toLowerCase();
    const roleText = (drive.role || '').toLowerCase();

    const techKeywords = [
        'java', 'spring', 'mysql', 'python', 'react', 'node', 'javascript',
        'data structures', 'algorithms', 'embedded', 'vlsi', 'system design',
        'c++', 'c', 'html', 'css', 'sql', 'mongodb', 'rest api', 'microcontrollers',
        'matlab', 'signal processing', 'oop', 'git'
    ];

    const matchedSkills = [];
    techKeywords.forEach(kw => {
        if (cvText.includes(kw)) {
            matchedSkills.push(kw.toUpperCase());
            score += 2.5;
        }
    });

    // Bonus for direct alignment with drive role
    if ((roleText.includes('software') || roleText.includes('sde') || roleText.includes('programmer')) &&
        (cvText.includes('java') || cvText.includes('python') || cvText.includes('data structures') || cvText.includes('c++'))) {
        score += 10;
    } else if ((roleText.includes('system') || roleText.includes('embedded')) &&
        (cvText.includes('embedded') || cvText.includes('vlsi') || cvText.includes('c') || cvText.includes('python'))) {
        score += 10;
    } else if ((roleText.includes('web') || roleText.includes('frontend') || roleText.includes('full stack')) &&
        (cvText.includes('react') || cvText.includes('javascript') || cvText.includes('node'))) {
        score += 10;
    }

    score = Math.max(15, Math.min(98, Math.round(score)));

    let level = 'high';
    let label = `📄 ${score}% CV Match (${matchedSkills.length} Skills Matched)`;
    if (score < 65) {
        level = 'low';
        label = `⚠️ ${score}% CV Match (Below Preferred Cutoff)`;
    } else if (score < 82) {
        level = 'medium';
        label = `📄 ${score}% CV Match (Good Resume Fit)`;
    }

    return { score, level, label, matchedSkills };
}

// AUTH
async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    try {
        const res = await fetch(API_LOGIN, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (res.ok) {
            currentUser = await res.json();
            loginModal.classList.add('hidden');
            loginForm.reset();
            updateAuthUI();
            showToast(`Welcome back, ${currentUser.fullName}!`);
        } else {
            const err = await res.json();
            showToast(`Login failed: ${err.error}`);
        }
    } catch (err) { showToast('Server connection error'); }
}

async function handleRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('regFullName').value.trim();
    const username = document.getElementById('regUsername').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value.trim();
    const role = document.getElementById('regRole').value;

    try {
        const res = await fetch(API_REGISTER, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ fullName, username, email, password, role })
        });

        if (res.ok) {
            currentUser = await res.json();
            registerModal.classList.add('hidden');
            registerForm.reset();
            updateAuthUI();
            showToast(`Registered as ${currentUser.fullName}!`);
        } else {
            const err = await res.json();
            showToast(`Registration failed: ${err.error}`);
        }
    } catch (err) { showToast('Server connection error'); }
}

function handleLogout() {
    currentUser = null;
    updateAuthUI();
    showToast('Logged out successfully');
}

function updateAuthUI() {
    if (currentUser) {
        authBar.classList.add('hidden');
        userBadge.classList.remove('hidden');
        userDisplayName.textContent = `${currentUser.fullName} (${currentUser.role})`;
        document.getElementById('userAvatar').textContent = currentUser.fullName.charAt(0).toUpperCase();
        switchRole(currentUser.role === 'COMPANY' ? 'COMPANY' : 'STUDENT');

        if (currentUser.role === 'STUDENT') {
            studentRollLookup.value = 'CS2026-042';
            lookupMyApplications();
            loadStudentProfileIntoForm('CS2026-042');
        }
    } else {
        authBar.classList.remove('hidden');
        userBadge.classList.add('hidden');
    }
    renderStudentDrives();
}

function initDemoShortcuts() {
    demoStudent1Btn.addEventListener('click', async () => {
        studentRollLookup.value = 'CS2026-042';
        lookupMyApplications();
        loadStudentProfileIntoForm('CS2026-042');
        showToast('Loaded Profile: Rahul Sharma (Java CV)');
    });

    demoStudent2Btn.addEventListener('click', async () => {
        studentRollLookup.value = 'EC2026-015';
        lookupMyApplications();
        loadStudentProfileIntoForm('EC2026-015');
        showToast('Loaded Profile: Priya Patel (Embedded CV)');
    });

    demoCompanyBtn.addEventListener('click', async () => {
        switchRole('COMPANY');
        document.getElementById('compName').value = 'Apple Inc.';
        document.getElementById('compRole').value = 'iOS Software Engineer';
        document.getElementById('compPackage').value = '22.5';
        document.getElementById('compCgpa').value = '7.5';
        document.getElementById('compLocation').value = 'Bangalore';
        document.getElementById('compStatus').value = 'OPEN';
        showToast('Populated sample recruitment drive');
    });
}

// FETCH DATA
async function fetchAllData() {
    await fetchDrives();
    await fetchApplications();
    await fetchStudentsDirectory();
}

async function fetchDrives() {
    try {
        const res = await fetch(API_DRIVES);
        if (res.ok) {
            drives = await res.json();
            updateKpiMetrics();
            renderStudentDrives();
            renderCompanyDrives();
        }
    } catch (err) { console.error('Fetch drives error:', err); }
}

async function fetchApplications() {
    try {
        const res = await fetch(API_APPS);
        if (res.ok) {
            applications = await res.json();
            updateKpiMetrics();
            renderCompanyApplications();
        }
    } catch (err) { console.error('Fetch applications error:', err); }
}

async function fetchStudentsDirectory() {
    try {
        const res = await fetch(API_STUDENTS);
        if (res.ok) {
            registeredStudents = await res.json();
            renderStudentDirectory();
            renderStudentDrives();
        }
    } catch (err) { console.error('Fetch students error:', err); }
}

function updateKpiMetrics() {
    kpiTotalDrives.textContent = drives.length;
    kpiApplications.textContent = applications.length;

    if (drives.length > 0) {
        const maxPackage = Math.max(...drives.map(d => d.packageLpa));
        const avgPackage = drives.reduce((acc, d) => acc + d.packageLpa, 0) / drives.length;
        kpiMaxCtc.textContent = `₹ ${maxPackage.toFixed(1)} LPA`;
        kpiAvgCtc.textContent = `₹ ${avgPackage.toFixed(1)} LPA`;
    } else {
        kpiMaxCtc.textContent = '₹ 0 LPA';
        kpiAvgCtc.textContent = '₹ 0 LPA';
    }
}

// STUDENT SIDE
async function loadStudentProfileIntoForm(rollNumber) {
    try {
        const res = await fetch(`${API_STUDENTS}?rollNumber=${encodeURIComponent(rollNumber)}`);
        if (res.ok) {
            const student = await res.json();
            document.getElementById('profRollNumber').value = student.rollNumber || '';
            document.getElementById('profStudentName').value = student.studentName || '';
            document.getElementById('profCgpa').value = student.cgpa || '';
            document.getElementById('profBranch').value = student.branch || '';
            document.getElementById('profEmail').value = student.email || '';
            document.getElementById('profPhone').value = student.phone || '';
            document.getElementById('profSkills').value = student.skills || '';
            profCvText.value = student.cvText || '';
            if (student.cvFilename) {
                currentCvFilename = student.cvFilename;
                fileNameDisplay.textContent = `📄 Uploaded CV: ${student.cvFilename}`;
            }
            renderStudentDrives();
        }
    } catch (err) { console.error(err); }
}

async function handleProfileSave(e) {
    e.preventDefault();
    const rollNumber = document.getElementById('profRollNumber').value.trim();
    const studentName = document.getElementById('profStudentName').value.trim();
    const cgpa = parseFloat(document.getElementById('profCgpa').value);
    const branch = document.getElementById('profBranch').value.trim();
    const email = document.getElementById('profEmail').value.trim();
    const phone = document.getElementById('profPhone').value.trim();
    const skills = document.getElementById('profSkills').value.trim();
    const cvText = profCvText.value.trim();
    const cvFilename = currentCvFilename || 'Resume.pdf';

    try {
        const res = await fetch(API_STUDENTS, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ rollNumber, studentName, cgpa, branch, email, phone, skills, cvFilename, cvText })
        });

        if (res.ok) {
            showToast('CV & Profile updated! Smart Match scores recalculated.');
            await fetchStudentsDirectory();
        } else { showToast('Failed to update profile'); }
    } catch (err) { showToast('Server error during profile save'); }
}

function renderStudentDrives() {
    const query = studentSearchInput.value.toLowerCase();
    const minPackage = parseFloat(document.getElementById('packageFilter')?.value || '0');
    const currentRoll = studentRollLookup.value.trim();
    const activeStudent = registeredStudents.find(s => s.rollNumber.toLowerCase() === currentRoll.toLowerCase());

    const filtered = drives.filter(d => {
        const matchesQuery = d.companyName.toLowerCase().includes(query) || d.role.toLowerCase().includes(query) || (d.location || '').toLowerCase().includes(query);
        const matchesPkg = d.packageLpa >= minPackage;
        return matchesQuery && matchesPkg;
    });

    if (filtered.length === 0) {
        studentDriveList.innerHTML = '<div class="empty-state"><p>No recruitment drives available.</p></div>';
        return;
    }

    studentDriveList.innerHTML = filtered.map(d => {
        const match = computeSmartMatchScore(activeStudent, d);
        const firstLetter = d.companyName.charAt(0).toUpperCase();

        return `
            <div class="pro-drive-card">
                <div class="card-top">
                    <div style="display:flex; align-items:center; gap:12px;">
                        <div class="comp-avatar">${firstLetter}</div>
                        <div>
                            <div class="comp-title">${escapeHtml(d.companyName)}</div>
                            <div class="role-subtitle">${escapeHtml(d.role)}</div>
                        </div>
                    </div>
                    <span class="ctc-badge">₹ ${d.packageLpa.toFixed(1)} LPA</span>
                </div>

                <div class="match-box">
                    <div class="match-header-row">
                        <span>CV Resume Match</span>
                        <span style="color:${match.level === 'high' ? '#10b981' : (match.level === 'medium' ? '#f59e0b' : '#f43f5e')}">${match.label}</span>
                    </div>
                    <div class="match-bar-track">
                        <div class="match-bar-fill ${match.level}" style="width: ${match.score}%;"></div>
                    </div>
                    ${match.matchedSkills && match.matchedSkills.length > 0 ? `
                        <div class="matched-skills-row">
                            <small style="color:#9ca3af; font-size:0.75rem;">Matched CV Keywords:</small>
                            ${match.matchedSkills.slice(0, 5).map(sk => `<span class="skill-pill">${sk}</span>`).join('')}
                        </div>
                    ` : ''}
                </div>

                <div class="drive-details">
                    <span>Location: ${escapeHtml(d.location || 'Bangalore')}</span>
                    <span>Min Cutoff CGPA: <strong>${d.minCgpa.toFixed(1)}</strong></span>
                </div>
                ${d.status !== 'OPEN' ? `
                    <button class="btn btn-secondary-pro" disabled>Drive ${d.status}</button>
                ` : (currentRoll && applications.some(a => a.driveId === d.id && a.rollNumber.toLowerCase() === currentRoll.toLowerCase()) ? `
                    <button class="btn btn-secondary-pro" style="color:#10b981; border-color:rgba(16,185,129,0.4);" disabled>Applied ✓</button>
                ` : `
                    <button class="btn btn-primary-gradient" onclick="openApplyModal(${d.id}, '${escapeHtml(d.companyName)}', '${escapeHtml(d.role)}')">
                        Apply Now
                    </button>
                `)}
            </div>
        `;
    }).join('');
}

function openApplyModal(driveId, companyName, role) {
    modalDriveId.value = driveId;
    modalDriveTitle.textContent = `Apply for ${companyName} (${role})`;
    
    const currRoll = studentRollLookup.value.trim();
    if (currRoll) {
        const student = registeredStudents.find(s => s.rollNumber.toLowerCase() === currRoll.toLowerCase());
        if (student) {
            document.getElementById('studentName').value = student.studentName;
            document.getElementById('rollNumber').value = student.rollNumber;
            document.getElementById('studentCgpa').value = student.cgpa;
            document.getElementById('branch').value = student.branch;
            document.getElementById('studentEmail').value = student.email;
        }
    }
    applyModal.classList.remove('hidden');
}

async function handleStudentApply(e) {
    e.preventDefault();
    const driveId = parseInt(modalDriveId.value);
    const studentName = document.getElementById('studentName').value.trim();
    const rollNumber = document.getElementById('rollNumber').value.trim();
    const cgpa = parseFloat(document.getElementById('studentCgpa').value);
    const branch = document.getElementById('branch').value.trim();
    const email = document.getElementById('studentEmail').value.trim();

    // Enforce 1 application per student per company drive
    const alreadyApplied = applications.some(a => a.driveId === driveId && a.rollNumber.toLowerCase() === rollNumber.toLowerCase());
    if (alreadyApplied) {
        showToast('⚠️ You have already applied for this company drive!');
        applyModal.classList.add('hidden');
        return;
    }

    try {
        const res = await fetch(API_APPS, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ driveId, studentName, rollNumber, cgpa, branch, email, applicationStatus: 'APPLIED' })
        });

        if (res.ok) {
            showToast('Application & CV submitted successfully!');
            applyModal.classList.add('hidden');
            fetchApplications();
            studentRollLookup.value = rollNumber;
            lookupMyApplications();
        } else {
            const err = await res.json();
            showToast(`Error: ${err.error || 'Submission failed'}`);
        }
    } catch (err) { showToast('Server error during application'); }
}

async function lookupMyApplications() {
    const roll = studentRollLookup.value.trim();
    if (!roll) {
        showToast('Please enter your Roll Number');
        return;
    }

    try {
        const res = await fetch(`${API_APPS}?rollNumber=${encodeURIComponent(roll)}`);
        if (res.ok) {
            const myApps = await res.json();
            if (myApps.length === 0) {
                myApplicationsList.innerHTML = '<p class="empty-state">No applications found for this Roll Number.</p>';
                return;
            }

            myApplicationsList.innerHTML = myApps.map(app => `
                <div class="app-row-card">
                    <div>
                        <strong style="display:block;">${escapeHtml(app.companyName)}</strong>
                        <span style="font-size:0.8rem; color:#9ca3af;">${escapeHtml(app.role)}</span>
                    </div>
                    <span class="badge ${app.applicationStatus}">${app.applicationStatus}</span>
                </div>
            `).join('');
        }
    } catch (err) { showToast('Failed to retrieve applications'); }
}

// COMPANY SIDE
async function handleCompanyDrivePost(e) {
    e.preventDefault();
    const companyName = document.getElementById('compName').value.trim();
    const role = document.getElementById('compRole').value.trim();
    const packageLpa = parseFloat(document.getElementById('compPackage').value);
    const minCgpa = parseFloat(document.getElementById('compCgpa').value) || 6.0;
    const location = document.getElementById('compLocation').value.trim() || 'Bangalore';
    const status = document.getElementById('compStatus').value;

    try {
        const res = await fetch(API_DRIVES, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ companyName, role, packageLpa, minCgpa, location, status })
        });

        if (res.ok) {
            showToast('Recruitment drive published to MySQL!');
            companyDriveForm.reset();
            fetchDrives();
        } else { showToast('Failed to post drive'); }
    } catch (err) { showToast('Server connection error'); }
}

function renderCompanyDrives() {
    companyDriveList.innerHTML = drives.map(d => `
        <div class="app-row-card">
            <div>
                <strong style="display:block; font-size:1rem;">${escapeHtml(d.companyName)}</strong>
                <span style="font-size:0.85rem; color:#9ca3af;">${escapeHtml(d.role)} • ₹ ${d.packageLpa.toFixed(1)} LPA</span>
            </div>
            <div style="display:flex; align-items:center; gap:10px;">
                <select style="padding:6px 10px; font-size:0.8rem;" onchange="updateDriveStatus(${d.id}, this.value)">
                    <option value="OPEN" ${d.status === 'OPEN' ? 'selected' : ''}>Open</option>
                    <option value="INTERVIEWING" ${d.status === 'INTERVIEWING' ? 'selected' : ''}>Interviewing</option>
                    <option value="CLOSED" ${d.status === 'CLOSED' ? 'selected' : ''}>Closed</option>
                </select>
                <button class="btn-icon-ghost" onclick="deleteCompanyDrive(${d.id})">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                </button>
            </div>
        </div>
    `).join('');
}

async function updateDriveStatus(id, newStatus) {
    try {
        const res = await fetch(`${API_DRIVES}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        if (res.ok) {
            showToast(`Drive status set to ${newStatus}`);
            fetchDrives();
        }
    } catch (err) { console.error(err); }
}

async function deleteCompanyDrive(id) {
    if (!confirm('Delete this placement drive?')) return;
    try {
        const res = await fetch(`${API_DRIVES}/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Drive deleted');
            fetchDrives();
        }
    } catch (err) { console.error(err); }
}

function renderCompanyApplications() {
    if (applications.length === 0) {
        companyAppList.innerHTML = '<div class="empty-state"><p>No student applications submitted yet.</p></div>';
        return;
    }

    const selectedStatus = document.getElementById('appStatusFilter')?.value || 'ALL';
    const filteredApps = applications.filter(a => selectedStatus === 'ALL' || a.applicationStatus === selectedStatus);

    if (filteredApps.length === 0) {
        companyAppList.innerHTML = `<div class="empty-state"><p>No applications found with status "${selectedStatus}".</p></div>`;
        return;
    }

    companyAppList.innerHTML = filteredApps.map(app => {
        const student = registeredStudents.find(s => s.rollNumber.toLowerCase() === app.rollNumber.toLowerCase());
        const drive = drives.find(d => d.id === app.driveId);
        const match = (student && drive) ? computeSmartMatchScore(student, drive) : { score: 80, level: 'high', label: '80% Match' };

        return `
            <div class="app-row-card">
                <div>
                    <strong>${escapeHtml(app.studentName)}</strong> (${escapeHtml(app.rollNumber)})
                    <div style="font-size:0.82rem; color:#9ca3af;">
                        Applying for: <strong>${escapeHtml(app.companyName)}</strong> - ${escapeHtml(app.role)}
                    </div>
                    <div style="font-size:0.78rem; color:#6b7280; margin-top:2px;">
                        Branch: ${escapeHtml(app.branch)} | CGPA: <strong>${app.cgpa.toFixed(2)}</strong> | Email: ${escapeHtml(app.email)}
                    </div>
                    <div style="font-size:0.78rem; font-weight:600; color:${match.level === 'high' ? '#10b981' : (match.level === 'medium' ? '#f59e0b' : '#f43f5e')}; margin-top:3px;">
                        📄 ${match.score}% CV Drive Match Score
                    </div>
                </div>
                <div class="action-btn-group">
                    <span class="badge ${app.applicationStatus}">${app.applicationStatus}</span>
                    <button class="btn-pill-sm btn-pill-cv" onclick="openCvModal('${escapeHtml(app.rollNumber)}', ${app.driveId})">📄 View CV</button>
                    <button class="btn-pill-sm btn-pill-shortlist" onclick="updateAppStatus(${app.id}, 'SHORTLISTED')">Shortlist</button>
                    <button class="btn-pill-sm btn-pill-select" onclick="updateAppStatus(${app.id}, 'SELECTED')">Select</button>
                    <button class="btn-pill-sm btn-pill-reject" onclick="updateAppStatus(${app.id}, 'REJECTED')">Reject</button>
                </div>
            </div>
        `;
    }).join('');
}

function exportApplicationsToCsv() {
    if (applications.length === 0) {
        showToast('No applications available to export.');
        return;
    }

    const headers = ['Application ID', 'Roll Number', 'Student Name', 'Company Name', 'Role', 'CGPA', 'Branch', 'Email', 'Application Status', 'CV Match Score %', 'Uploaded CV Filename'];
    const rows = applications.map(app => {
        const student = registeredStudents.find(s => s.rollNumber.toLowerCase() === app.rollNumber.toLowerCase());
        const drive = drives.find(d => d.id === app.driveId);
        const match = (student && drive) ? computeSmartMatchScore(student, drive) : { score: 80 };
        const cvFile = student ? (student.cvFilename || 'Resume.pdf') : 'N/A';

        return [
            app.id,
            `"${app.rollNumber}"`,
            `"${app.studentName}"`,
            `"${app.companyName}"`,
            `"${app.role}"`,
            app.cgpa,
            `"${app.branch}"`,
            `"${app.email}"`,
            `"${app.applicationStatus}"`,
            `${match.score}%`,
            `"${cvFile}"`
        ].join(',');
    });

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `Placement_Applicants_CV_Report_${new Date().toISOString().slice(0,10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    showToast('📥 Applicant CV report exported as CSV!');
}

function renderStudentDirectory() {
    if (registeredStudents.length === 0) {
        studentDirectoryList.innerHTML = '<div class="empty-state"><p>No registered student profiles.</p></div>';
        return;
    }

    studentDirectoryList.innerHTML = registeredStudents.map(s => `
        <div class="app-row-card">
            <div>
                <strong>${escapeHtml(s.studentName)}</strong> (${escapeHtml(s.rollNumber)})
                <div style="font-size:0.82rem; color:#d1d5db;">
                    Branch: ${escapeHtml(s.branch)} | CGPA: <strong>${s.cgpa.toFixed(2)}</strong>
                </div>
                <div style="font-size:0.78rem; color:#9ca3af;">
                    Email: ${escapeHtml(s.email)} | Uploaded CV: <strong>${escapeHtml(s.cvFilename || 'Resume.pdf')}</strong>
                </div>
                ${s.cvText ? `<div style="font-size:0.78rem; color:#a78bfa; margin-top:2px;">CV Excerpt: ${escapeHtml(s.cvText.substring(0, 95))}...</div>` : ''}
            </div>
            <div>
                <button class="btn-pill-sm btn-pill-cv" onclick="openCvModal('${escapeHtml(s.rollNumber)}')">📄 Inspect CV</button>
            </div>
        </div>
    `).join('');
}

function openCvModal(rollNumber, driveId = null) {
    const cvPreviewModal = document.getElementById('cvPreviewModal');
    const cvModalContent = document.getElementById('cvModalContent');
    const student = registeredStudents.find(s => s.rollNumber.toLowerCase() === rollNumber.toLowerCase());

    if (!student) {
        showToast('Student profile not found');
        return;
    }

    let matchInfoHtml = '';
    if (driveId) {
        const drive = drives.find(d => d.id === driveId);
        if (drive) {
            const match = computeSmartMatchScore(student, drive);
            matchInfoHtml = `
                <div style="background: rgba(99, 102, 241, 0.12); border:1px solid rgba(99,102,241,0.3); padding:12px 14px; border-radius:8px; margin-top:10px;">
                    <strong style="color:#a5b4fc;">CV Match Breakdown for ${escapeHtml(drive.companyName)} (${escapeHtml(drive.role)}):</strong>
                    <div style="font-size:1.05rem; font-weight:700; color:${match.level === 'high' ? '#10b981' : (match.level === 'medium' ? '#f59e0b' : '#f43f5e')}; margin-top:4px;">
                        ${match.label}
                    </div>
                    ${match.matchedSkills && match.matchedSkills.length > 0 ? `
                        <div style="margin-top:6px; display:flex; gap:4px; flex-wrap:wrap; align-items:center;">
                            <small style="color:#9ca3af;">Key Skills Matched:</small>
                            ${match.matchedSkills.map(sk => `<span class="skill-pill">${sk}</span>`).join('')}
                        </div>
                    ` : ''}
                </div>
            `;
        }
    }

    const cvFilename = student.cvFilename || 'Uploaded_Resume.pdf';
    const cvText = student.cvText || 'No CV text extracted yet.';

    cvModalContent.innerHTML = `
        <div style="font-size:0.88rem; color:#d1d5db; line-height:1.6;">
            <div>Student Name: <strong>${escapeHtml(student.studentName)}</strong> (${escapeHtml(student.rollNumber)})</div>
            <div>Academic Standing: <strong>${escapeHtml(student.branch)}</strong> | CGPA: <strong>${student.cgpa.toFixed(2)}</strong></div>
            <div>Contact: ${escapeHtml(student.email)} | Phone: ${escapeHtml(student.phone || 'N/A')}</div>
            <div style="margin-top:6px; color:#a78bfa;">Uploaded CV Document: 📄 <strong>${escapeHtml(cvFilename)}</strong></div>
        </div>
        ${matchInfoHtml}
        <div style="font-weight:600; font-size:0.85rem; color:#9ca3af; margin-top:14px;">📄 Parsed CV Resume Content:</div>
        <div class="cv-preview-box">${escapeHtml(cvText)}</div>
        <div style="margin-top:12px; font-size:0.8rem; color:#a5b4fc;">Skills Summary: ${escapeHtml(student.skills || 'None listed')}</div>
    `;

    cvPreviewModal.classList.remove('hidden');
}

async function updateAppStatus(appId, newStatus) {
    try {
        const res = await fetch(`${API_APPS}/${appId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ applicationStatus: newStatus })
        });

        if (res.ok) {
            showToast(`Status set to ${newStatus}`);
            fetchApplications();
        }
    } catch (err) { console.error(err); }
}

function showToast(msg) {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.textContent = msg;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
