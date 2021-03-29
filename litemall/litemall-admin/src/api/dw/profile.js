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

const DeleteTag = '/tag/basicTag/deleteTag'
export function deleteTag(data) {
  return request({
    url: DeleteTag,
    method: 'delete',
    data
  })
}

const UpdatePrimaryTag = '/tag/basicTag/updatePrimary'
export function updatePrimaryTag(data) {
  return request({
    url: UpdatePrimaryTag,
    method: 'put',
    data
  })
}

const UpdateModelTagRule = '/tag/basicTag/updateModelTagRule'
export function updateModelTagRule(data) {
  return request({
    url: UpdateModelTagRule,
    method: 'put',
    data
  })
}

const UpdateModelTag = '/tag/basicTag/updateModelTag'
export function updateModelTag(data) {
  return request({
    url: UpdateModelTag,
    method: 'put',
    data
  })
}

const SubmitModelList = '/tag/tagModel/submitModelList'
export function listSubmitModel() {
  return request({
    url: SubmitModelList,
    method: 'get'
  })
}

const ApproveModel = '/tag/tagModel/approveModel'
export function approveModel(data) {
  return request({
    url: ApproveModel,
    method: 'put',
    data
  })
}
