<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">Personalized Recommendations</h1>
    <hr>
    <#if user?? && user.name??>
        <div class="greeting">
            <h2 style="text-align: center">Welcome, ${user.name}!</h2>
        </div>
    <#else>
        <div class="greeting">
            <h2 style="text-align: center">User not found or not logged in!</h2>
        </div>
    </#if>
    <div class="card-columns">
        <#list recommendedProducts as product>
            <a href="/product/${product.id}" class="product-link">
                <div class="card text-white bg-dark my-3 product">
                    <#if product.previewImageId??>
                        <img src="/images/${product.previewImageId}" class="card-img-top">
                    <#else>
                        <div class="card-img-top not-found-image">Not found</div>
                    </#if>
                    <div class="card-body">
                        <h5 class="card-title">${product.title}</h5>
                        <p class="card-text">${product.price} Tenge</p>
                    </div>
                    <div class="card-footer">
                        ${product.city}
                    </div>
                </div>
            </a>
        </#list>
    </div>

</@t.template>
