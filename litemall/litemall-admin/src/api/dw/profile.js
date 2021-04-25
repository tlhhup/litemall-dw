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
export function listSubmitModel(query) {
  return request({
    url: SubmitModelList,
    method: 'get',
    params: query
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

const FinishModel = '/tag/tagModel/finishModel/'
export function finishModel(tagId) {
  return request({
    url: FinishModel + tagId,
    method: 'put'
  })
}

const PublishModel = '/tag/tagModel/publishModel'
export function publishModel(data) {
  return request({
    url: PublishModel,
    method: 'put',
    data
  })
}

const OfflineModel = '/tag/tagModel/offlineModel'
export function offlineModel(data) {
  return request({
    url: OfflineModel,
    method: 'put',
    data
  })
}

const RunOrStopModel = '/tag/tagModel/runOrStopModel'
export function runOrStopModel(data) {
  return request({
    url: RunOrStopModel,
    method: 'put',
    data
  })
}

const MergeTagList = '/tag/mergeTag/list'
export function mergeTagList(query) {
  return request({
    url: MergeTagList,
    method: 'get',
    params: query
  })
}

const MergeTagDetail = '/tag/mergeTag/detail'
export function mergeTagDetail(id) {
  return request({
    url: MergeTagDetail + '/' + id,
    method: 'get'
  })
}

const CreateMergeTag = '/tag/mergeTag/create'
export function createMergeTag(data) {
  return request({
    url: CreateMergeTag,
    method: 'post',
    data
  })
}

const RemoveMergeTag = '/tag/mergeTag/remove'
export function removeMergeTag(id) {
  return request({
    url: RemoveMergeTag + '/' + id,
    method: 'delete'
  })
}

const UpdateMergeTag = '/tag/mergeTag/update'
export function updateMergeTag(data) {
  return request({
    url: UpdateMergeTag,
    method: 'put',
    data
  })
}

const UserTagSearch = '/tag/userTag/search'
export function searchUserTag(query) {
  return request({
    url: UserTagSearch,
    method: 'get',
    params: query
  })
}
