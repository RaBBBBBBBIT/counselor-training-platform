import request from '@/utils/request'

// 题库
export function listBank(query) { return request({ url: '/api/v1/question-banks/list', method: 'get', params: query }) }
export function addBank(data) { return request({ url: '/api/v1/question-banks', method: 'post', data }) }
export function updateBank(data) { return request({ url: '/api/v1/question-banks', method: 'put', data }) }
export function delBank(bankId) { return request({ url: '/api/v1/question-banks/' + bankId, method: 'delete' }) }

// 题目
export function listQuestion(query) { return request({ url: '/api/v1/questions/list', method: 'get', params: query }) }
export function addQuestion(data) { return request({ url: '/api/v1/questions', method: 'post', data }) }
export function updateQuestion(data) { return request({ url: '/api/v1/questions', method: 'put', data }) }
export function delQuestion(questionId) { return request({ url: '/api/v1/questions/' + questionId, method: 'delete' }) }

// 试卷
export function listPaper(query) { return request({ url: '/api/v1/papers/list', method: 'get', params: query }) }
export function genRandomPaper(data) { return request({ url: '/api/v1/papers/random', method: 'post', data }) }
export function genFixedPaper(data) { return request({ url: '/api/v1/papers/fixed', method: 'post', data }) }
export function delPaper(paperId) { return request({ url: '/api/v1/papers/' + paperId, method: 'delete' }) }
