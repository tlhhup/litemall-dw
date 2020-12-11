import request from '@/utils/request'

const AppraiseBadTopNList = '/dw/adsAppraiseBadTopn/list'
export function listAppraiseBadTopn(query) {
  return request({
    url: AppraiseBadTopNList,
    method: 'get',
    params: query
  })
}

const AdsDateTopicList = '/dw/dashBoard/list'
export function listAdsDateTopic(query) {
  return request({
    url: AdsDateTopicList,
    method: 'get',
    params: query
  })
}

const AdsDateTopicChart = '/dw/dashBoard/chart'
export function chartDuration(query) {
  return request({
    url: AdsDateTopicChart,
    method: 'get',
    params: query
  })
}

const uploadedDataURL = '/dw/adsRegionDayCount/list'
export function chartRegion() {
  return request({
    url: uploadedDataURL,
    method: 'get'
  })
}
