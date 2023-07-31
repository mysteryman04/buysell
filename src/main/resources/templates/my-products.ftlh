<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">My Listings</h1>
    <hr>
    <a class="btn btn-dark mt-3 mb-3" style="text-align: center; width: 100%"
       data-toggle="collapse" href="#collapseExample" role="button"
       aria-expanded="true" aria-controls="collapseExample">
        Add new item
    </a>
    <div class="collapse mb-3" id="collapseExample" style="">
        <form action="/product/create" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="productName">Title</label>
                <input type="text" required class="form-control"
                       id="productName" name="title">
            </div>
            <div class="form-group">
                <label for="price">Price</label>
                <div class="input-group mb-3">
                    <input type="number" required class="form-control" id="price" name="price">
                    <div class="input-group-append">
                        <span class="input-group-text">Tenge</span>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label for="searchCity">Location</label>
                <select id="searchCity" required name="city" class="form-control">
                    <option value="">Choose parameter</option>
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
            <div class="form-group">
                <label for="description">Item description</label>
                <textarea required class="form-control" id="description" name="description"></textarea>
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input type="file" name="file1" id="file1">
                    <label class="custom-file-label" for="file1">Preview image to item</label>
                </div>
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input type="file" name="file2" id="file2">
                    <label class="custom-file-label" for="file2">Second image to iteem</label>
                </div>
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input type="file" name="file3" id="file3">
                    <label class="custom-file-label" for="file3">Third image to item</label>
                </div>
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}"/>

            <button type="submit" style="width: 100%" class="btn btn-dark">Add</button>
        </form>
    </div>
    <div class="card-columns">
        <#list products as product>
            <a href="/product/${product.id}" class="product-link">
                <div class="card text-white bg-dark my-3 product">
                    <div class="card-top">
                        <form action="/product/delete/${product.id}" method="post">
                            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                            <button type="submit" class="btn btn-danger m-3">
                                Delete
                            </button>
                        </form>
                    </div>
                    <img src="/images/${product.previewImageId}"
                         class="card-img-top">
                    <div class="card-body">
                        <h5 class="card-title">${product.title}</h5>
                        <p class="card-text">${product.price} ₽</p>
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