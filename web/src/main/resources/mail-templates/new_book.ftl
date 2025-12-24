<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
        .container { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); max-width: 600px; margin: auto; }
        .header { text-align: center; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 20px; }
        .footer { font-size: 12px; color: #777; text-align: center; margin-top: 20px; border-top: 1px solid #ddd; padding-top: 10px; }
        .rare { color: #d35400; font-weight: bold; border: 1px solid #d35400; padding: 5px; display: inline-block; margin-top: 10px; }
        .info-table { width: 100%; border-collapse: collapse; }
        .info-table td { padding: 8px; border-bottom: 1px solid #eee; }
        .label { font-weight: bold; color: #555; }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <img src="https://raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png"
             alt="BookApp Logo" style="width: 150px;">
        <h2>Додано нову книгу!</h2>
    </div>

    <p>Вітаємо! Каталог успішно поповнився новим виданням.</p>

    <table class="info-table">
        <tr>
            <td class="label">Назва:</td>
            <td>${title}</td>
        </tr>
        <tr>
            <td class="label">Автор:</td>
            <td>${author}</td>
        </tr>
        <tr>
            <td class="label">Рік видання:</td>
            <td>${year?c}</td>
        </tr>
        <tr>
            <td class="label">Дата додавання:</td>
            <td>${added}</td>
        </tr>
    </table>

    <#if year < 2000>
        <div class="rare">
            &#9733; УВАГА! Це раритетне видання! &#9733;
        </div>
    </#if>

    <div class="footer">
        <p>BookApp © 2025. Цей лист згенеровано автоматично.</p>
    </div>
</div>
</body>
</html>