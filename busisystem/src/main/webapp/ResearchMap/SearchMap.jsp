<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <link rel="stylesheet" href="css/jquery.mCustomScrollbar.css"/>
    <link rel="stylesheet" href="css/searchMap.css"/>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<title></title>
	<meta name="description" id="content1">
	<meta name="title" id="content2">
	<meta name="sharecontent" data-msg-content=""/>
</head>
<body>
<div id="popupMap">
    <div style="position: absolute;opacity: 0;filter: alpha(opacity=0);"><img src="images/300.png"></div>
    <div id="popupMapBox">
        <div class="mapcenter"></div>
    </div>
</div>
<script type="text/javascript" src="javascript/jquery.min.js"></script>
<script type="text/javascript" src="javascript/jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript" src="javascript/html2canvas.js"></script>
<script type="text/javascript" src="javascript/share.js"></script>
<script type="text/javascript" src="../md5.js"></script>
<script>
function getNowFormatDate(){var date=new Date();var seperator1="-";var seperator2=":";var month=date.getMonth()+1;var strDate=date.getDate();if(month>=1&&month<=9){month="0"+month}if(strDate>=0&&strDate<=9){strDate="0"+strDate}var currentdate=date.getFullYear()+seperator1+month+seperator1+strDate+" "+date.getHours()+seperator2+date.getMinutes()+seperator2+date.getSeconds();return currentdate};
</script>
<script>
    $(function(){
        //国际化开始
        $.get('javascript/language.json',function(result){
            var languageData  = result["zh-CN"];
            if(window.parent.nRequire){
                const [path,mateAPI,electron,mateInnerAPI] = [window.parent.nRequire('path'),window.parent.nRequire('mateAPI'),window.parent.nRequire('electron'),window.parent.nRequire('mateInnerAPI')];
                const [mateFile,platformLanguage] = [mateAPI.use('mateFile'),mateInnerAPI.appManager.getPlatformLanguage()];
                function changeLanguage(platformLanguage){
                    var data = mateFile.readFileSync({path:path.join(electron.remote.app.getAppPath(),'conf','language.json')});
                    data = JSON.parse(data);
                    languageData = data[platformLanguage];
                    staticLanguage(languageData);
                }
                function staticLanguage(data){
                    $("#searchMap2").text(data.searchMap);
                    $("title").text(data.TRUMateserMap);
                    $("#content1").attr("content",data.content1);
                    $("#content1").attr("title",data.content1);
                }
                changeLanguage(platformLanguage);

                electron.ipcRenderer.on('languageChange',function(event, flag,arg){
                    var platformLanguage = mateInnerAPI.appManager.getPlatformLanguage();
                    changeLanguage(platformLanguage);
                });
            }
            var time= getNowFormatDate();
            var m=hex_md5("busiSystemMate" + time);
            var id=window.location.href.split('?')[1].match(/id=[0-9]+/)[0].split('=')[1];
            if(window.parent.window.nRequire)var ipcRenderer =window.parent.window.nRequire('electron').ipcRenderer;
            if(/share/.test(window.location.search)){
                var share=true;
            }
            var data={
                busiBlock:"searchBlock",
                Md5Str:m,
                time:time,
                busiCode:"querySearchMapsDatas",
                id:id
            }
            $.ajax({
                type : "get",
                dataType:'json',
                url: "../entry/busiEntry.do",
                data: {
                    paramObject:JSON.stringify(data)
                },
                success:function(data){
                    console.log('asas   '+data);
                    if(data.code== 0){
                        var html=data.result;
                        _html="";
                        $('meta[name=description]').attr('content',languageData.sharedSearchResult1+'“'+html.maps.firstKeyword+'”'+languageData.sharedSearchResult2);
                        $('meta[name=sharecontent]').attr('data-msg-content',languageData.sharedSearchResult1+'“'+html.maps.firstKeyword+'”'+languageData.sharedSearchResult2);
                        $('.mapcenter').attr('data-id',html.maps.id).attr('data-userid',html.maps.userId);
                        for(var i=0;i<html.mapKeywords.length;i++) {
                            if(i==0){
                                var fir_html='<div class="maplistbox fir">\
                                                <div class="maptitle" data-firstkeyword="'+html.maps.firstKeyword+'" data-id="'+html.maps.id+'">\
                                                    <span id="searchMap2">'+languageData.searchMap+'</span><i class="heng"></i>\
                                                </div>';
                            }else{
                                var fir_html='<div class="maplistbox">';
                            }
                            _html+=fir_html+'<div class="keyword">\
                                                <a href="javascript:;" title="'+decodeURIComponent(html.mapKeywords[i].keyword)+'" data-id="'+html.mapKeywords[i].id+'" data-source="'+html.mapKeywords[i].source+'"><span>('+(function(id,i,dataHtml){var ss=0;dataHtml.mapDatas.forEach(function(o){if(o.keywordId==id){ss++}});return ss})(html.mapKeywords[i].id,i,html)+')</span>'+(i+1)+'、'+decodeURIComponent(html.mapKeywords[i].keyword)+'</a><i class="shu"></i>\
                                            </div>\
                                            <div class="browse">\
                                                <i class="heng"></i>\
                                                <ul>'+(function(id,html){
                                        var _html=""
                                        for(var n=0;n<html.mapDatas.length;n++){
                                            if(html.mapDatas[n].keywordId==id){
                                                _html+='<li><i class="curr'+html.mapDatas[n].keepFlag+'" data-id="'+html.mapDatas[n].id+'"></i><a href="'+html.mapDatas[n].url+'" title="'+decodeURIComponent(html.mapDatas[n].title)+'">'+decodeURIComponent(html.mapDatas[n].title)+'</a></li>'
                                            }
                                        }
                                        return _html+'</ul>'
                                    })(html.mapKeywords[i].id,html)+
                                '</div>\
                            </div>'
                        }
                        $('.mapcenter').html(_html);
                        if(window.parent.document.getElementsByTagName('iframe').length!=0)window.parent.bindZ();
                        resize();
                    }
                    $('body').on('click','.browse a',function(){
                        if(window.parent.document.getElementsByTagName('iframe').length!=0){
                            var showmaindata={
                                url:$(this).attr('href'),
                                mapUrl:$('.mapcenter').attr('data-id'),
                                showNav:"detail",
                                tid:-1
                            }
                            console.log("ipc");
                            ipcRenderer.send('showContextFrameWindow',showmaindata);
                        }else{
                            console.log("open");
                            window.open($(this).attr('href'));
                        }
                        return false;
                    })
                    $('div.browse').mCustomScrollbar({
                        theme:"minimal-dark"
                    });
                    $("div.mapcenter").mCustomScrollbar({
                        theme:"minimal-dark"
                    });
                }
            });
            $('div.mapcenter').on('click','div.keyword a',function(){
                var data={
                        firstkeyword:$('div.maptitle').attr('data-firstkeyword'),
                        mapId:$('div.maptitle').attr('data-id'),
                        keyword:$(this).text().replace(/^\([0-9]+\)[0-9]+、/,""),
                        keywordId:"00"+new Date().getTime(),
                        sourceId:$(this).attr('data-source')
                    }
                ipcRenderer.send('showMainFrameWindow',data);
            });
            $(window).resize(function(){
                resize();
            });
        })
    });
    function resize(){
        $('.browse ul li a').css('width',$(window).width()-360);
        $('.mapcenter').height($(window).height()-20);
    }
</script>
</body>
</html>