'use strict'

document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('itemContainer');
    const items = JSON.parse(container.dataset.items || '[]');
    const toppings = JSON.parse(container.dataset.toppings || '[]');
    let itemCount = 0;

    function buildOptions() {
        return items.map(function(item) {
            return `<option value="${item.id}">${item.name}</option>`;
        }).join('');
    }

    function buildSizeOption(){
        return `
        <option value="M">M</option>
        <option value="L">L</option>
        `;
    }

    function buildToppingCheckboxes(rowIndex) {
        return toppings.map(function(topping) {
            return `
                <label class="topping-checkbox">
                    <input type="checkbox" name="items[${rowIndex}].toppingIds" value="${topping.id}" />
                    ${topping.name}
                </label>
            `;
        }).join('');
    }

    function addRow() {
        const rowIndex = itemCount;

        const newRow = document.createElement('div');
        newRow.className = 'item-row';
        newRow.innerHTML = `
            <select name="items[${rowIndex}].productId">
                ${buildOptions()}
            </select>
            <input type="number" name="items[${rowIndex}].quantity" min="1" value="1" />
            <select name="items[${rowIndex}].size">
                ${buildSizeOption()}
            </select>
            <span class="topping-list">
                ${buildToppingCheckboxes(rowIndex)}
            </span>
            <button type="button" class="remove-btn">削除</button>
        `;

        container.appendChild(newRow);
        itemCount++;
    }

    // 最初から1行は表示しておく
    addRow();

    document.getElementById('addItemBtn').addEventListener('click', addRow);

    // イベント委譲：動的に追加した行の削除ボタンにも対応する
    container.addEventListener('click', function(e) {
        if (!e.target.classList.contains('remove-btn')) {
            return;
        }

        const rows = container.querySelectorAll('.item-row');
        if (rows.length <= 1) {
            // 最低1行は残す
            return;
        }

        e.target.closest('.item-row').remove();
    });
});
