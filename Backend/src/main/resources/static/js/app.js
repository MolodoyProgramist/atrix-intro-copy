// API functions
async function startQuiz(utmParams = {}) {
    const response = await fetch('/api/quiz/start', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(utmParams)
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();

    if (!result.success) {
        throw new Error(result.error || 'Failed to start quiz');
    }

    return result.data;
}

async function getQuizResults(sessionId) {
    const response = await fetch(`/api/quiz/${sessionId}/results`);

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();

    if (!result.success) {
        throw new Error(result.error || 'Failed to get results');
    }

    return result.data;
}