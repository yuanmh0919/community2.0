$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");

    // 获取标题和内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    //发送异步请求
    $.post(
        CONTEXT_PATH + "/discuss/add",
        {
            "title": title,
            "content": content
        },
        function (data) {
            data = $.parseJSON(data);
            $("#hintBody").text(data.msg);

            $("#hintModal").modal("show");
            //2秒之后自动关闭提示框
            setTimeout(function () {
                $("#hintModal").modal("hide");
                //刷新页面
                if (data.code == 0){
                    location.reload();
                }
            }, 2000);
        }
    );


}