from flask import *
import pymysql
from werkzeug.utils import secure_filename

obj=Flask(__name__)

con=pymysql.connect(host='localhost',port=3306,user='root',passwd='',db='machub')
cmd=con.cursor()
obj.secret_key="sss"


@obj.route('/')
def main():
    return render_template('LOGIN.html')

@obj.route('/login',methods=['post'])
def login():
    uname=request.form['textfield']
    password = request.form['textfield2']
    cmd.execute(" select * from `Login` where email='"+uname+"' and password='"+password+"'")
    s=cmd.fetchone()
    if s is None:
        return '''<script>alert("Invalid user name or password");window.location='/'</script>'''
    elif s[3]=='admin':
        return '''<script>alert("Successfully login");window.location='/ADMIN_HOME'</script>'''
    elif s[3]=='machub':
        return '''<script>alert("Successfully login");window.location='/machub'</script>'''
    elif s[3]=='staff':
        return '''<script>alert("Successfully login");window.location='/STAFF HOME'</script>'''
    else:
        return  '''<script>alert("Invalid "):window.location'/'</script>'''


@obj.route('/ADMIN_HOME')
def ADMIN_HOME():
    return render_template('ADMIN HOME.html')

@obj.route('/REGISTRATION')
def REGISTRATION():
    return render_template('REGISTRATION.html')

@obj.route('/REGISTRATION2',methods=['post'])
def REGISTRATION2():

    fname=request.form['textfield']
    lname=request.form['textfield2']
    staffcategory=request.form['select']
    gender=request.form['radiobutton']
    place=request.form['textfield3']
    post=request.form['textfield4']
    pin=request.form['textfield5']
    phone=request.form['textfield6']
    email=request.form['textfield7']
    password=request.form['textfield8']
    confirmpassword=request.form['textfield9']
    cmd.execute("INSERT INTO Login VALUES(null,'"+email+"','"+password+"','staff')")
    s=con.insert_id()
    cmd.execute("insert into staff values(null,'"+str(s)+"','"+fname+"','"+lname+"','"+staffcategory+"','"+gender+"','"+place+"','"+post+"','"+pin+"','"+phone+"','"+email+"')")
    con.commit()
    return'''<script> alert("Registered");window.location="/ADMIN_HOME"</script>'''



@obj.route('/MANAGE STAFF')
def MANAGE_STAFF():
 cmd.execute("SELECT `staff` .*,`login`.`type` FROM `login` JOIN `staff`  ON `login`.`loginid`=`staff`.`loginid` WHERE `login`.`type`='staff'")
 s=cmd.fetchall()
 print(s)
 return render_template('MANAGE STAFF.html',val=s)

@obj.route('/DELETE STAFF',methods=['post','get'])
def DELETE_STAFF():
    id=request.args.get('lid')
    session['lid']=id
    print(id)
    cmd.execute("update login set login.type='delete' where loginid='"+str(id)+"'")
    con.commit()
    return '''<script> alert("deleted");window.location=' /MANAGE STAFF'</script>'''

@obj.route('/EDIT_STAFF')
def EDIT_STAFF():
    id=request.args.get('lid')
    session['lid']=id
    cmd.execute("SELECT * from `staff` where loginid='"+str(session['lid'])+"'" )
    p= cmd.fetchone()
    cmd.execute("select * from staff")
    s=cmd.fetchall()
    print(p)
    return render_template('STAFF EDIT.html', val=p,vals=s)

@obj.route('/EDIT_STAFF2',methods=['post'])
def EDIT_STAFF2():
    lid=session['lid']
    fname=request.form['textfield']
    lname = request.form['textfield2']
    staffcategory = request.form['select']
    gender = request.form['radiobutton']
    place = request.form['textfield3']
    post = request.form['textfield4']
    pin = request.form['textfield5']
    phone = request.form['textfield6']
    email = request.form['textfield7']
    cmd.execute("update staff set fname='"+fname+"',lname='"+lname+"',staffcategory='"+staffcategory+"',gender='"+gender+"',place='"+place+"',post='"+post+"',pin='"+pin+"',phone='"+phone+"',email='"+email+"' where loginid='"+lid+"' ")
    con.commit()
    return '''<script> alert("updated successfully ");window.location=' /MANAGE STAFF'</script>'''


@obj.route('/ADD_CAT', methods=['post','get'])
def ADD_CAT():
    return render_template('ADD CATEGORY.html')

@obj.route('/ADD_CAT2',methods=['post','get'])
def ADD_CAT2():
       category=request.form['textfield']
       cmd.execute("insert into category values(null,'"+category+"','pending')")
       con.commit()
       return '''<script> alert("Category added");window.location="/ADD_CAT"</script>'''



@obj.route('/ADD_ITEM')
def ADD_ITEM():
    cmd.execute("select * from category")
    s=cmd.fetchall()
    return render_template('ADD ITEM.html',val=s)

@obj.route('/ADD_ITEM2',methods=['post','get'])
def ADD_ITEM2():
       category=request.form['select']
       itemname=request.form['textfield3']
       rate=request.form['textfield']
       quantity=request.form['textfield2']
       image=request.files['file']
       description=request.form['textarea']
       fname=secure_filename(image.filename)
       image.save('static/upload/' + fname)
       cmd.execute("insert into item values(null,'"+category+"','"+itemname+"','"+rate+"','"+quantity+"','"+fname+"','"+description+"','pending')")
       con.commit()
       return '''<script> alert("item added successfully");window.location="/ADD_ITEM"</script>'''


@obj.route('/VIEW_CAT')
def view_cat():
   cmd.execute("select * FROM category WHERE `category` . `status` !='deleted'")
   s1=cmd.fetchall()
   return render_template('VIEW CATEGORY.html',val=s1)

@obj.route('/delete_category',methods=['post','get'])
def delete_cat():
    id=request.args.get('categoryid')
    cmd.execute("UPDATE `category` SET `category`.`status`='deleted' where categoryid='"+str(id)+"'")
    con.commit()
    return'''<script>alert("deleted");window.location="/VIEW_CAT"</script>'''

@obj.route('/VIEW_ITEM')
def view_item():
    cmd.execute("SELECT `item` .*,`category`.`category` FROM `category` JOIN `item` ON `category`.`categoryid`=`item`.`categoryid` where item.status='pending'")
    s1=cmd.fetchall()
    return render_template('VIEW ITEM.html',val=s1)

@obj.route('/edit_item',methods=['post','get'])
def edit_item():
    id=request.args.get('itemid')
    session['eid']=id
    print(id)
    cmd.execute("SELECT * from `item` where itemid='" + str(session['itemid']) + "'")
    s = cmd.fetchone()
    cmd.execute("select * from category ")
    p = cmd.fetchall()
    return render_template('EDIT ITEM.html',val=s,vals=p)

@obj.route('/edit_item2', methods=['post', 'get'])
def edit_item2 ():
 try:
    itemname=request.form['textfield']
    category=request.form['select']
    rate=request.form['textfield3']
    quantity=request.form['textfield4']
    image=request.files['file']
    ename = secure_filename(image.filename)
    image.save('static/upload/' + ename)
    description=request.form['textarea2']
    cmd.execute("update item set categoryid='"+category+"',itemname='"+itemname+"',rate='"+rate+"',quantity='"+quantity+"',image='"+ename+"',description='"+description+"','pending'")
    con.commit()
    return '''<script> alert ("updated");window.location="/VIEW_ITEM"</script>'''
 except Exception as e:
     itemname = request.form['textfield']
     category = request.form['select']
     rate = request.form['textfield3']
     quantity = request.form['textfield4']
     description = request.form['textarea2']
     cmd.execute("update item set categoryid='" + category + "',itemname='" + itemname + "',rate='" + rate + "',quantity='" + quantity +  "',description='" + description + "' where itemid='" + str(session['eid']) + "'")
     con.commit()
     return '''<script> alert ("updated");window.location="/VIEW_ITEM"</script>'''
@obj.route('/delete_item',methods=['post','get'])
def delete_item():
    id=request.args.get('itemid')
    cmd.execute("UPDATE `item` SET `item`.status='deleted' where itemid='"+str(id)+"'")
    con.commit()
    return'''<script> alert ("deleted");window.location="/VIEW_ITEM"</script>'''




# @obj.route('/EDIT STAFF')
# def EDIT_STAFF():
#     return render_template('/MANAGE STAFF')
#

@obj.route('/STAFF UPDATE PROFILE')
def STAFF_UPDATE_PROFILE():
    return render_template('STAFF UPDATE PROFILE.html')

@obj.route('/ADD STOCK')
def ADD_STOCK():
    return render_template('ADD STOCK.html')

@obj.route('/VIEW STOCK')
def VIEW_STOCK():
    return render_template('VIEW STOCK.html')

@obj.route('/STAFF HOME')
def STAFF_HOME():
    return render_template('STAFF HOME.html')


if __name__ =='__main__':
  obj.run(debug=True)