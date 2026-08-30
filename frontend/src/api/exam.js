import request from '@/utils/request'
export function myPractices(){return request({url:'/api/v1/exams/practices',method:'get'})}
export function myCompetitions(){return request({url:'/api/v1/exams/competitions',method:'get'})}
export function getPaper(paperId){return request({url:'/api/v1/exams/papers/'+paperId,method:'get'})}
export function answerOne(d){return request({url:'/api/v1/exams/answers',method:'post',data:d})}
export function submitAll(d){return request({url:'/api/v1/exams/submissions',method:'post',data:d})}
export function getStagePaper(cid,sid){return request({url:'/api/v1/exams/competitions/'+cid+'/stages/'+sid+'/paper',method:'get'})}
export function submitCompetition(cid,sid,d){return request({url:'/api/v1/exams/competitions/'+cid+'/stages/'+sid+'/submit',method:'post',data:d})}
