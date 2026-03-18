'use strict';

$(function() {
    // 退会ボタンがクリックされた時の処理
    $(document).on('click', '#delete-btn', function(e) {
        // 確認ダイアログを表示
        const result = confirm('本当に退会しますか？\n退会すると、これまでの注文履歴などのデータが確認できなくなります。');
        
        if (!result) {
            // 「キャンセル」なら送信を中止
            e.preventDefault();
            return false;
        }
        // 「OK」ならそのまま form が POST 送信される
    });
});
