import axios from 'axios'

var instance = axios.create({
  //baseURL: 'http://log-center',
  timeout: 1000
});

export function start(){
	instance({
		url:'/process',
		method:'post',
		data:{
		  "action": "2",
		  "ar": "MX",
		  "ba": "Huawei",
		  "detail": "542",
		  "en": "start",
		  "extend1": "",
		  "g": "09466258@gmail.com",
		  "hw": "640*960",
		  "l": "es",
		  "loadingTime": "4",
		  "md": "Huawei-15",
		  "mid": "1098",
		  "os": "8.1.2",
		  "sr": "P",
		  "sv": "V2.8.3",
		  "t": new Date().getTime(),
		  "uid": `${window.localStorage.getItem('userId') || ''}`,
		  "vc": "12",
		  "vn": "1.1.5"
		}
	})
}

// 只处理加购和收藏事件
var events=new Array()

export function addEvent(event){
	events.push(event)
}

function sendEvents(){
    if(events.length==0){
        return
    }
	//console.info('发送事件日志')
	var data={
		  "cm": {
		    "sv": "V2.7.9",
		    "os": "8.0.3",
		    "g": "848972@gmail.com",
		    "mid": "1099",
		    "l": "pt",
		    "vc": "16",
		    "hw": "640*960",
		    "ar": "MX",
		  	"t": new Date().getTime(),
		  	"uid": `${window.localStorage.getItem('userId') || ''}`,
		    "md": "sumsung-19",
		    "vn": "1.3.1",
		    "ba": "Sumsung",
		    "sr": "X"
		  },
		  "ap": "app",
		  "et": events
		}
	var timestamp=new Date().getTime()
	var log=timestamp+"|"+JSON.stringify(data)
	//console.info(log)
	instance({
		url:'/process',
		method:'post',
		data:log
	})
	//清空数组
	events.length=0
}

var schedule;
export function setSchedule(){
	//5分钟发送一次事件日志
	schedule=window.setInterval(sendEvents,5*60000)
}

export function clearSchedule(){
	window.clearInterval(schedule)
}