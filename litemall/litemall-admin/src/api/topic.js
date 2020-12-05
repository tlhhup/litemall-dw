import request from '@/utils/request'

export function listTopic(query) {
  return request({
    url: '/admin/topic/list',
    method: 'get',
    params: query
  })
}

export function createTopic(data) {
  return request({
    url: '/admin/topic/create',
    method: 'post',
    data
  })
}

export function readTopic(query) {
  return request({
    url: '/admin/topic/read',
    method: 'get',
    params: query
  })
}

export function updateTopic(data) {
  return request({
    url: '/admin/topic/update',
    method: 'post',
    data
  })
}

export function deleteTopic(data) {
  return request({
    url: '/admin/topic/delete',
    method: 'post',
    data
  })
}

export function batchDeleteTopic(data) {
  return request({
    url: '/admin/topic/batch-delete',
    method: 'post',
    data
  })
}
