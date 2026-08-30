import request from '@/utils/request'
export function listScore(q){return request({url:'/api/v1/scores/list',method:'get',params:q})}
export function importSubjective(d){return request({url:'/api/v1/scores/subjective/import',method:'post',data:d})}
export function exportSubjective(){return request({url:'/api/v1/scores/subjective/export',method:'get'})}
