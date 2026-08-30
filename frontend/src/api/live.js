import request from '@/utils/request'
export function xueqingDraw(){return request({url:'/api/v1/live-matches/xueqing/draw',method:'get'})}
export function xueqingAnswer(id){return request({url:'/api/v1/live-matches/xueqing/'+id,method:'get'})}
export function caseDraw(d){return request({url:'/api/v1/live-matches/case-analysis/draw',method:'post',data:d})}
export function talkDraw(){return request({url:'/api/v1/live-matches/talk/draw',method:'post'})}
