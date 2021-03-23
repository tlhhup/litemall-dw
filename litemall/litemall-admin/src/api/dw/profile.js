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

const SearchTag = '/tag/basicTag/searchTag'
export function searchTag(name) {
  return request({
    url: SearchTag + '/' + name,
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

const ModelTagCreate = '/tag/basicTag/modelTag'
export function createModel(data) {
  return request({
    url: ModelTagCreate,
    method: 'post',
    data
  })
}

const ModelRuleCreate = '/tag/basicTag/saveModelRule'
export function createModelRule(data) {
  return request({
    url: ModelRuleCreate,
    method: 'post',
    data
  })
}

const ChildTag = '/tag/basicTag/childTags'
export function childTags(query) {
  return request({
    url: ChildTag,
    method: 'get',
    params: query
  })
}
