<!-- recommendations.ftlh -->
<#import "blocks/template.ftlh" as t>

<#if user?has_content>
<@t.template user>
    <h1 style="text-align: center">Personalized Recommendations</h1>
    <hr>
    <div class="card-columns">
        <#if recommendedProducts?size == 0>
            <h1 style="text-align: center">No recommendations found.</h1>
        <#else>
            <#list recommendedProducts as product>
                <a href="/product/${product.id}" class="product-link">
                    <div class="card text-white bg-dark my-3 product">
                        <img src="/images/${product.previewImageId}" class="card-img-top">
                        <div class="card-body">
                            <h5 class="card-title">${product.title}</h5>
                            <p class="card-text">${product.price} ₽</p>
                        </div>
                        <div class="card-footer">
                            ${product.city}
                        </div>
                    </div>
                </a>
            </#list>
        </#if>
    </div>
    <h1>Welcome, ${user.name}!</h1>
</@t.template>
<#else>
    <h1>User not found or not logged in!</h1>
</#if>
