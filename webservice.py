from flask import *
import pymysql
from werkzeug.utils import secure_filename

obj=Flask(__name__)

con=pymysql.connect(host='localhost',port=3306,user='root',passwd='',db='machub')
cmd=con.cursor()
@obj.route('/login',methods=['post'])
def login():
    email=request.form['uname']
    print(email)
    password = request.form['password']
    print(password)
    cmd.execute(" select * from `Login` where email='"+email+"' and password='"+password+"'")
    s=cmd.fetchone()
    print(s)
    if s is None:
        return jsonify({'task': 'invalid'})
    else:
        return jsonify({'task':str(s[0])+"#"+s[3]})



@obj.route('/shop_reg',methods=['post'])
def shop_reg():

    shopname=request.form['shopname']
    place=request.form['place']
    post=request.form['post']
    pin=request.form['pin']
    phone=request.form['phone']
    email=request.form['uname']
    password=request.form['password']
    confirmpassword=request.form['confirm password']
    if password==confirmpassword:
        cmd.execute("INSERT INTO Login VALUES(null,'"+email+"','"+password+"','shop')")
        s=con.insert_id()
        cmd.execute("insert into shop values(null,'"+str(s)+"','"+shopname+"','"+phone+"','"+place+"','"+post+"','"+pin+"','"+email+"')")
        con.commit()
        return jsonify({'task':'success'})
    else:
        return jsonify({'task':'mismatch'})

@obj.route('/Category',methods=['post'])
def Category():
    # category = request.form['category']
    cmd.execute(" select * from `category` ")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)


@obj.route('/allitems',methods=['post'])
def allitems():
    # category = request.form['category']
    cmd.execute(" select * from `item` where itemid in (select productcategory.itemid from productcategory where accessoryid='5' )")
    row_headers = [x[0] for x in cmd.description]

    results = cmd.fetchall()
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)
@obj.route('/Item',methods=['post'])
def item():
    cid = request.form['cid']
    aid = request.form['aid']

    print(cid,"ciddd")
    cmd.execute("SELECT `item`.*,`offer`.`offer`,`startdate`,`enddate`,DATEDIFF(CURDATE(),startdate) sd,DATEDIFF(CURDATE(),enddate) ed FROM `item` LEFT JOIN `offer` ON `offer`.`itemid`=`item`.`itemid` JOIN `productcategory` ON `productcategory`.itemid=item.itemid WHERE `productcategory`.accessoryid='"+aid+"' AND item.status='pending' AND `item`.`categoryid`='"+str(cid)+"'")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)
@obj.route('/viewprofile',methods=['post','get'])
def viewprofile():
    id = request.form['lid']
    print(id)
    cmd.execute("SELECT * FROM shop WHERE `loginid`='" + str(id) + "'")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    json_data = []
    for result in results:
        row = []
        for r in result:
            row.append(str(r))
        json_data.append(dict(zip(row_headers, row)))
    con.commit()
    print(json_data)
    return jsonify(json_data)

@obj.route('/updateprofile',methods=['GET','POST'])
def updateprofile():
    shopname = request.form['shopname']
    phone = request.form['phone']
    place = request.form['place']
    post = request.form['post']
    pincode = request.form['pincode']
    email= request.form['email']
    lid = request.form['lid']
    cmd.execute("update shop set shopname='"+shopname+"',phone='"+phone+"',place='"+place+"',post='"+post+"',pin='"+pincode+"',email='"+email+"' where loginid='"+str(lid)+"'")
    con.commit()
    return jsonify({'task': "Success"})


@obj.route('/offer',methods=['GET','POST'])
def offer():
        id = request.form['lid']
        print(id)
        cmd.execute("SELECT `offer`.* FROM `offer` JOIN `item` ON `item`.`itemid`=`offer`.`itemid`" )
        row_headers = [x[0] for x in cmd.description]
        results = cmd.fetchall()
        json_data = []
        for result in results:
            row = []
            for r in result:
                row.append(str(r))
            json_data.append(dict(zip(row_headers, row)))
        con.commit()
        print(json_data)
        return jsonify(json_data)

@obj.route('/shop_cart',methods=['GET','POST'])
def shop_cart():
    itemid = request.form['itemid']
    qty = request.form['qty']
    rate = request.form['rate']
    shpid = request.form['shpid']
    cmd.execute("SELECT `quantity` FROM item WHERE `itemid`='" + str(itemid) + "'")
    r = cmd.fetchone()
    orgqunty = float(r[0])
    print(orgqunty)
    print(qty)
    if orgqunty < float(qty):
        return jsonify({'task': "Out of stock"})
    else:
        qqqtyy = orgqunty - float(qty)
        cmd.execute("INSERT INTO `cart` VALUES(null,'"+itemid+"','"+qty+"','"+rate+"','"+str(shpid)+"',0)")
        cmd.execute("UPDATE `item` SET `quantity`='" + str(qqqtyy) + "'  WHERE `itemid`='" + str(itemid) + "'")
        con.commit()
        return jsonify({'task': "Success"})

@obj.route('/cart_items',methods=['GET','POST'])
def cart_items():
        id = request.form['lid']
        print(id)
        cmd.execute("SELECT `item`.`itemname`,item.image,`cart`.* FROM `cart` JOIN `item` ON `item`.`itemid`=`cart`.`itemid` AND `cart`.`shpid`='"+str(id)+"' AND billid=0" )
        row_headers = [x[0] for x in cmd.description]
        results = cmd.fetchall()
        json_data = []
        for result in results:
            row = []
            for r in result:
                row.append(str(r))
            json_data.append(dict(zip(row_headers, row)))
        con.commit()
        print(json_data)
        return jsonify(json_data)

@obj.route('/place_order',methods=['GET','POST'])
def place_order():
    lid = request.form['lid']
    tot=0
    cmd.execute("SELECT * FROM `cart` WHERE shpid='"+lid+"' and billid=0")
    s=cmd.fetchall()
    for  i in s:
        tot+=int(i[3])
    cmd.execute("SELECT MAX(`orders`.`oid`)+1 FROM `orders`")
    s = cmd.fetchone()
    print(s)

    if s[0] is None:
        id = 1
        print(id)
    else:
        id = (s[0])
    cmd.execute("insert into orders values('"+str(id)+"','"+lid+"','"+str(tot)+"',curdate(),'ordered')")
    cmd.execute("update cart set billid='"+str(id)+"' where  shpid='"+lid+"'  and billid=0")
    con.commit()
    return jsonify({'task': "Success",'tot':str(tot),'oid':str(id)})

@obj.route('/payment', methods=['POST'])
def payment():

    userid=request.form['lid']
    print(userid)
    ifsc = request.form['ifsc']
    accno = request.form['account_no']
    bank=request.form['bank']
    totamt=request.form['tot']
    oid=request.form['oid']

    cmd.execute("select * from payment_tb where bank='"+bank+"' and ifsc='"+ifsc+"' and accno='"+accno+"' and `payment_tb`.`user_id`='"+str(userid)+"'")
    ss=cmd.fetchone()
    if ss is not None:
        if int(ss[5])>int(totamt):
            balance = int(ss[5]) - int(totamt)
            cmd.execute("update payment_tb set `payment_tb`.`balance`='" + str(balance) + "' where `payment_tb`.`user_id`='"+str(userid)+"'")
            cmd.execute("UPDATE `order` SET `status`='payed' WHERE oid='"+oid+"'")
            con.commit()
            return jsonify({'task': "Success"})
        else:
            return jsonify({'task': "insufficient amount"})

    else:
        return jsonify({'task': "Re-Enter Account Details"})

@obj.route('/addfeadback',methods=['POST'])
def addfeadback():
    userid=request.form['uid']
    feadback=request.form['feedback']
    cmd.execute("insert into feadback values(null,'"+userid+"','"+feadback+"',curdate())")
    con.commit()
    return  jsonify({"task":"success"})
@obj.route('/addcomplaint',methods=['POST'])
def addcomplaint():
    userid=request.form['uid']
    complaint=request.form['complaint']

    cmd.execute("insert into complaint values(null,'"+userid+"','"+complaint+"',curdate(),'pending')")
    con.commit()
    return  jsonify({"task":"success"})
@obj.route('/viewreply',methods=['POST'])
def viewreply():
    con=pymysql.connect(host="localhost",port=3306,user="root",password="",db="machub")
    cmd=con.cursor()
    userid=request.form['uid']
    print(userid)
    cmd.execute("select * from complaint where complaint.uid='"+str(userid)+"'")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)

@obj.route('/service_reqst',methods=['POST'])
def service_reqst():
    shpid=request.form['shpid']
    itemid=request.form['itemid']
    reqst = request.form['reqst']
    warranty = request.form['warranty']
    cmd.execute("insert into Service_request values(null,'"+shpid+"','"+itemid+"','"+reqst+"','"+warranty+"','pending')")
    con.commit()
    return  jsonify({"task":"success"})



@obj.route('/shopviewreply',methods=['POST'])
def shopviewreply():
    con=pymysql.connect(host="localhost",port=3306,user="root",password="",db="machub")
    cmd=con.cursor()
    userid=request.form['uid']
    print(userid)
    cmd.execute("SELECT `item`.`itemname`,`service_request`.* FROM `item` JOIN `service_request` ON `service_request`.`itemid`=`item`.`itemid` AND `service_request`.`shopid`='"+str(userid)+"'")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)

@obj.route('/orderstatus',methods=['POST'])
def orderstatus():
    con=pymysql.connect(host="localhost",port=3306,user="root",password="",db="machub")
    cmd=con.cursor()
    userid=request.form['uid']
    print(userid)
    cmd.execute("SELECT * FROM `order` WHERE shpid='"+str(userid)+"'")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)


@obj.route('/viewassigned',methods=['POST'])
def viewassigned():
    con=pymysql.connect(host="localhost",port=3306,user="root",password="",db="machub")
    cmd=con.cursor()
    agid=request.form['agid']
    print(agid)
    cmd.execute("SELECT `orders`.`oid`,(total+total*(18/100)) total,date,`shop`.`shopname`,`phone`,`place`,`post`,`pin`,assign_work.asid FROM `orders` JOIN `shop` ON `shop`.`loginid`=`orders`.`shpid` JOIN `assign_work` ON `assign_work`.`oid`=`orders`.`oid` AND `assign_work`.`deladid`='"+agid+"' AND `assign_work`.`status`!='delivered'")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)

@obj.route('/vieworderitems',methods=['POST'])
def vieworderitems():
    con=pymysql.connect(host="localhost",port=3306,user="root",password="",db="machub")
    cmd=con.cursor()
    oid=request.form['oid']
    print(oid)
    cmd.execute("SELECT `item`.`itemname`,`cart`.`qty`,`tot_price`,item.image,item.itemid FROM `cart` JOIN item ON `cart`.`itemid`=`item`.`itemid`  JOIN orders ON orders.oid=cart.billid WHERE item.itemid NOT IN(SELECT returnitem.productid FROM returnitem WHERE returnitem.billid='"+oid+"') AND orders.status='delivered' AND `cart`.billid='"+oid+"'")

    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)
@obj.route('/update_status',methods=['POST'])
def update_status():
    assid=request.form['assid']
    status=request.form['status']

    cmd.execute("update `assign_work` SET `status`='"+status+"' WHERE `asid`='"+assid+"'")
    cmd.execute("update `orders` SET `status`='"+status+"' WHERE `oid` IN (select assign_work.oid from assign_work WHERE asid='"+assid+"')")
    con.commit()
    return  jsonify({"task":"success"})

@obj.route('/calculate',methods=['POST'])
def calculate():
    shpid=request.form['lid']
    cmd.execute("SELECT *,(total+total*(18/100)) gst FROM `orders` WHERE `shpid`='"+shpid+"'")
    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)
@obj.route('/deletecart',methods=['POST'])
def deletecart():
    cartid=request.form['cid']
    cmd.execute("SELECT * FROM `cart` WHERE cartid='"+cartid+"'")
    s=cmd.fetchone()
    if s is not None:
        itemid=s[1]
        quantity=s[2]
        cmd.execute("SELECT `quantity` FROM item WHERE `itemid`='" + str(itemid) + "'")
        r = cmd.fetchone()
        orgqunty = float(r[0])+float(quantity)
        cmd.execute("update item set quantity='"+ str(orgqunty)+"' where itemid='"+str(itemid)+"'")
        cmd.execute(" delete from cart WHERE cartid='"+cartid+"'")
        con.commit()
        return jsonify({"task": "success"})
    else:
        return jsonify({"task": "invalid"})

@obj.route('/viewproducts', methods=['POST'])
def viewproducts():

        cmd.execute("select * from accessory")
        row_headers = [x[0] for x in cmd.description]
        results = cmd.fetchall()
        print(results)
        json_data = []
        for result in results:
            json_data.append(dict(zip(row_headers, result)))
        con.commit()
        print(json_data)
        return jsonify(json_data)
@obj.route('/returnpolicy',methods=['POST'])
def returnpolicy():
    oid=request.form['oid']
    itemid=request.form['itemid']
    cmd.execute("SELECT DATEDIFF(CURDATE(),orders.date)FROM orders WHERE oid='"+oid+"'")
    s=cmd.fetchone()
    if s is not None:
        d=s[0]
        if d > 7:
            return jsonify({"task": "date expired"})
        else :
         cmd.execute("insert into returnitem values(null,'"+itemid+"','"+oid+"',curdate())")
         con.commit()
         return  jsonify({"task":"success"})
@obj.route('/billvieworderitems',methods=['POST'])
def billvieworderitems():
    con=pymysql.connect(host="localhost",port=3306,user="root",password="",db="machub")
    cmd=con.cursor()
    oid=request.form['oid']
    print(oid)
    cmd.execute("SELECT `item`.`itemname`,`cart`.`qty`,`tot_price`,item.image,item.itemid FROM `cart` JOIN item ON `cart`.`itemid`=`item`.`itemid`  JOIN orders ON orders.oid=cart.billid AND (orders.status='ordered' OR orders.status='delivered')  AND `cart`.billid='"+oid+"'")

    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)
@obj.route('/VIEWDELIVERYRETURN',methods=['POST'])
def Viewdeliveryreturn():
    con=pymysql.connect(host="localhost",port=3306,user="root",password="",db="machub")
    cmd=con.cursor()
    did=request.form['did']
    print(did)
    cmd.execute("SELECT item.itemname,item.itemid,image,cart.qty,tot_price,returnorder.returnorderid FROM returnitem JOIN item ON returnitem.productid=item.itemid  JOIN cart ON cart.itemid=item.itemid AND cart.billid =returnitem.billid JOIN returnorder ON returnorder.returnid=returnitem.returnid WHERE returnorder.deladid='"+did+"' AND returnorder.status='pending'")

    row_headers = [x[0] for x in cmd.description]
    results = cmd.fetchall()
    print(results)
    json_data = []
    for result in results:
        json_data.append(dict(zip(row_headers, result)))
    con.commit()
    print(json_data)
    return jsonify(json_data)
@obj.route('/deliveryreturn',methods=['POST'])
def deliveryreturn():
    rid=request.form['rid']
    itemid = request.form['itemid']
    qty = request.form['qty']


    cmd.execute("update `returnorder` SET `status`='returned' WHERE `returnorderid`='"+rid+"'")
    cmd.execute("update `item` SET `quantity`=quantity+ '"+str(qty)+"' WHERE `itemid` ='"+str(itemid)+"' ")
    con.commit()
    return  jsonify({"task":"success"})
@obj.route('/cartqty',methods=['POST'])
def cartqty():
    cid=request.form['cid']

    qty= request.form['quantity']

    cmd.execute("select * from cart where cartid='"+cid+"'")
    s=cmd.fetchone()
    if s is not None:
        qtys=s[2]
        if int(qty) > int(qtys):
           cmd.execute("update item set quantity=quantity+'"+str(qty)+"' where itemid='"+str(s[1])+"'")
           con.commit()
        else:
            cmd.execute("update item set quantity=quantity-'" + str(qty) + "' where itemid='" + str(s[1]) + "'")
            con.commit()
        cmd.execute("select item.rate from item where  itemid='" + str(s[1]) + "'")
        res=cmd.fetchone()
        if res is not None:
            tot_price=float(qty)*float(res[0])
        cmd.execute("update cart set qty ='"+str(qty)+"' ,tot_price='"+str(tot_price)+"'where cartid='"+str(cid)+"'")


        con.commit()

        return  jsonify({"task":"success"})
    else:
        return jsonify({"task": "invalid"})
@obj.route('/viewallproducts', methods=['POST'])
def viewallproducts():

        cmd.execute("SELECT `item`.*,`offer`.`offer`,`startdate`,`enddate`,DATEDIFF(CURDATE(),startdate) sd,DATEDIFF(CURDATE(),enddate) ed FROM `item` LEFT JOIN `offer` ON `offer`.`itemid`=`item`.`itemid`  AND item.status='pending' ")
        row_headers = [x[0] for x in cmd.description]
        results = cmd.fetchall()
        print(results)
        json_data = []
        for result in results:
            json_data.append(dict(zip(row_headers, result)))
        con.commit()
        print(json_data)
        return jsonify(json_data)



if(__name__=='__main__'):
    obj.run(host='0.0.0.0',port=5000)