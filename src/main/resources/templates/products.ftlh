<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">Search for items</h1><hr>
    <form method="get" action="/">
        <div class="form-row">
            <div class="form-group col-md-2">
                <label for="searchCity">Location</label>
                <select id="searchCity" class="form-control">
                    <option value="Almaty">Almaty</option>
                    <option value="Astana">Astana</option>
                    <option value="Shymkent">Shymkent</option>
                    <option value="Oskemen">Oskemen</option>
                    <option value="Atyrau">Atyrau</option>
                    <option value="Aktobe">Aktobe</option>
                    <option value="Petropavl">Petropavl</option>
                    <option value="Semey">Semey</option>
                    <option value="Aktau">Aktau</option>
                    <option value="Pavlodar">Pavlodar</option>
                    <option value="Kostanay">Kostanay</option>
                    <option value="Karagandy">Karagandy</option>
                    <option value="Zhezkazgan">Zhezkazgan</option>
                    <option value="Oral">Oral</option>
                    <option value="Aktau">Aktau</option>
                    <option value="Kyzylorda">Kyzylorda</option>
                    <option value="Kokshetau">Kokshetau</option>
                    <option value="Taraz">Taraz</option>
                    <option value="Turkistan">Turkistan</option>
                </select>
            </div>
            <div class="form-group col-md-10">
                <label for="searchWord">Search in stock</label>
                <input type="text" name="searchWord" <#if searchWord??>value="${searchWord}"</#if> class="form-control"
                       id="searchWord">
            </div>
            <button style="width: 100%" type="submit" class="btn btn-dark">Search</button>
        </div>
    </form>
    <hr>
    <div class="card-columns">
        <#list products as product>
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
        <#else>
            <h1 style="text-align: center">Not found</h1>
        </#list>
    </div>
</@t.template>