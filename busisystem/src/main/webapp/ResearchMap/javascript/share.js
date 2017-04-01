(function($){
    //国际化开始
    $.get('javascript/language.json',function(result){
        var languageData = result["zh-CN"];
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
                $('title').text(data.details);
                $("#details").text(data.details);
                $("#searchMap").text(data.searchMap);
                $("#shareto").text(data.shareTo);
                $("#scan").attr("alt",data.scan);
            }
            changeLanguage(platformLanguage);

            electron.ipcRenderer.on('languageChange',function(event, flag,arg){
                var platformLanguage = mateInnerAPI.appManager.getPlatformLanguage();
                changeLanguage(platformLanguage);
            });
        }
        //国际化结束
        $.fn.share = function ($options,sites) {
            var $image = $(document).find('img:first').attr('src');
            var $config = {
                url: $options["url"] || window.location.href,
                desc: $options["desc"] || document.title,
                title: $options["title"] || document.title,
                summary: $(document.head).find('[name="title"]').attr('content'),
                image: $image ? $image : '',
                pics: $options["pics"] || "",
                showcount : $options["showcount"] || 1,
                target : $options["target"] || '_blank'
            };
            var windowParams = (function(){
                var screenWidth = screen.width;
                var screenHeight = screen.height;
                var commonParams = "toolbar=no,menubar=no,depended=yes,scrollbars=no,resizable=no,location=no,status=no";
                return {
                    qzone: commonParams + ",height=430,width=600,top="+ (screenHeight - 400)/2 +",left=" + (screenWidth - 600)/2,
                    qq: commonParams + ",height=600,width=800,top="+ (screenHeight - 600)/2 +",left=" + (screenWidth - 800)/2
                };
            })();
            $(this).bind("click",function(){
                var saveFile = function(data){
                    var url = encodeURIComponent(data);
                    console.log(sites);
                    var data={
                        busiBlock:"searchBlock",
                        busiCode:"saveSearchShare",
                        userId:$(window.frames["main02"].document).find('.mapcenter').attr('data-userid'),
                        mapId: $(window.frames["main02"].document).find('.mapcenter').attr('data-id'),
                        shareTo:sites == "qq" ? 3 : 4,
                        picUrl:url,
                        device:"pc",
                        language:"ZH"
                    }
                    $.ajax({
                        type: "post",
                        url: "../saveCanvas.do",
                        data: data,
                        dataType:"text",
                        success: function(result){
                            console.log(result);
                        },
                        error:function(result){
                            console.log(result);
                        }
                    });
                };
                html2canvas($(window.frames["main02"].document).find('.mapcenter').get(0), {
                    allowTaint: true,
                    taintTest: false,
                    onrendered: function(canvas) {
                        var imgData = canvas.toDataURL();
                        saveFile(imgData);
                    }
                });
                var s = [];
                for(var i in $config){
                    s.push(i + '=' + encodeURIComponent($config[i]||''));
                };
                var $urls = {
                    qzone: "http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?" + s.join("&"),
                    qq: "http://connect.qq.com/widget/shareqq/index.html?" + s.join("&")
                };
                window.open($urls[sites],sites + "shareWin",windowParams[sites]);
            });
        };
    })

})(jQuery);