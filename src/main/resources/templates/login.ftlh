<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">Authorization</h1>
    <hr>
    <form action="/login " method="post">
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" class="form-control <#if myOptionalVar??>when-present<#else>when-missing</#if>" id="email" name="username">
            <div id="email" class="invalid-feedback">
                Email is incorrect
            </div>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" class="form-control <#if myOptionalVar??>when-present<#else>when-missing</#if>" id="password" name="password">
            <div id="password" class="invalid-feedback">
                Password is incorrect
            </div>
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit" style="width: 100%" class="btn btn-dark">Log in</button>
    </form>
    <hr>
    <form action="/registration " method="get">
    <button type="submit" style="width: 100%" class="btn btn-danger">Sign Up</button>
    </form>
</@t.template>