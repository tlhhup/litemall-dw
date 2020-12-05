import request from '@/utils/request'

export function listAftersale(query) {
  return request({
    url: '/admin/aftersale/list',
    method: 'get',
    params: query
  })
}

export function receptAftersale(data) {
  return request({
    url: '/admin/aftersale/recept',
    method: 'post',
    data
  })
}

export function batchReceptAftersale(data) {
  return request({
    url: '/admin/aftersale/batch-recept',
    method: 'post',
    data
  })
}

export function rejectAftersale(data) {
  return request({
    url: '/admin/aftersale/reject',
    method: 'post',
    data
  })
}

export function batchRejectAftersale(data) {
  return request({
    url: '/admin/aftersale/batch-reject',
    method: 'post',
    data
  })
}

export function refundAftersale(data) {
  return request({
    url: '/admin/aftersale/refund',
    method: 'post',
    data
  })
}
