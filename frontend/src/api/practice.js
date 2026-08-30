import request from '@/utils/request'
export function listPractice(q){return request({url:'/api/v1/practice-batches/list',method:'get',params:q})}
export function addPractice(d){return request({url:'/api/v1/practice-batches',method:'post',data:d})}
export function updatePractice(d){return request({url:'/api/v1/practice-batches',method:'put',data:d})}
export function delPractice(id){return request({url:'/api/v1/practice-batches/'+id,method:'delete'})}
