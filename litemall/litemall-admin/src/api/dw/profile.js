import request from '@/utils/request'

const BasicTagTreeList = '/tag/basicTag/list'
export function listBasicTagTree() {
  return request({
    url: BasicTagTreeList,
    method: 'get'
  })
}

const PrimaryTagTree = '/tag/basicTag/primaryTagTree'
export function oneLevelTag() {
  return request({
    url: PrimaryTagTree,
    method: 'get'
  })
}

const PrimaryTagCreate = '/tag/basicTag/primaryTag'
export function createPrimaryTag(data) {
  return request({
    url: PrimaryTagCreate,
    method: 'post',
    data
  })
}
